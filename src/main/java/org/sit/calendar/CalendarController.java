package org.sit.calendar;

import org.sit.calendar.data.PlanData;
import org.sit.calendar.data.UserData;
import org.sit.calendar.data.repositories.PlanDataRepository;
import org.sit.calendar.data.repositories.UserDataRepository;
import org.sit.calendar.forms.AddForm;
import org.sit.calendar.forms.LoginForm;
import org.sit.calendar.forms.SignUpForm;
import org.sit.calendar.session.LoginUserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Controller
@Transactional
@EntityScan("org.sit.calendar.data")
@EnableJpaRepositories("org.sit.calendar.data.repositories")
@SessionAttributes(value = "loginUserSession", types = LoginUserSession.class)
public class CalendarController {
	private final PasswordEncoder passwordEncoder;
	private final LoginUserSession loginUserSession;
	private final UserDataRepository userDataRepository;
	private final PlanDataRepository planDataRepository;
	
	@RequestMapping(value = {"", "/index"})
	@Transactional
	public String index(Model model) {
		if (loginUserSession.isLoggedIn()) {
			model.addAttribute("loggedIn", true);
			LocalDate currentMonthFirstDay = LocalDate.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), 1);
			LocalDate lastDay = currentMonthFirstDay.plusMonths(1).minusDays(1);
			DayOfWeek firstDayOfWeek = currentMonthFirstDay.getDayOfWeek();
			List<List<DateObject>> currentMonth = new CopyOnWriteArrayList<>();
			currentMonth.add(new CopyOnWriteArrayList<>());
			int diff1 = DayOfWeek.SATURDAY.compareTo(firstDayOfWeek);
			for (int i = 0; i < diff1; i++) {
				LocalDate localDate = currentMonthFirstDay.minusDays(diff1 - i);
				currentMonth.get(currentMonth.size() - 1).add(new DateObject(localDate.getDayOfMonth(), localDate.getDayOfWeek()));
				if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
					currentMonth.add(new CopyOnWriteArrayList<>());
				}
			}
			for (int i = 0; i < lastDay.getDayOfMonth(); i++) {
				LocalDate localDate = currentMonthFirstDay.plusDays(i);
				currentMonth.get(currentMonth.size() - 1).add(new DateObject(localDate.getDayOfMonth(), localDate.getDayOfWeek()));
				if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
					currentMonth.add(new CopyOnWriteArrayList<>());
				}
			}
			int diff2 = DayOfWeek.SATURDAY.compareTo(lastDay.getDayOfWeek());
			for (int i = 0; i < diff2; i++) {
				LocalDate localDate = currentMonthFirstDay.plusDays(i);
				currentMonth.get(currentMonth.size() - 1).add(new DateObject(localDate.getDayOfMonth(), localDate.getDayOfWeek()));
			}
			Set<PlanData> planDataSet = userDataRepository.findById(loginUserSession.getUserData().getId()).get().getPlanDataSet();
			planDataSet = planDataSet.parallelStream().filter(planData -> planData.getLocalDateTime().isAfter(LocalDateTime.of(currentMonthFirstDay, LocalTime.MIN))).filter(planData -> planData.getLocalDateTime().isBefore(LocalDateTime.of(lastDay, LocalTime.MAX))).collect(Collectors.toSet());
			for (PlanData planData : planDataSet) {
				int index = planData.getLocalDateTime().getDayOfMonth() + diff1 - 1;
				int i = index / 7;
				int j = index % 7;
				currentMonth.get(i).get(j).getPlanData().add(planData);
			}
			model.addAttribute("currentMonth", currentMonth);
		} else {
			model.addAttribute("loggedIn", false);
		}
		return "index";
	}
	
	@Transactional
	public boolean loginAuth(String loginUserName, String loginPassword) {
		Optional<UserData> data = userDataRepository.findOneByName(loginUserName);
		if (data.isPresent()) {
			if (passwordEncoder.matches((loginPassword), data.get().getPasswordHash())) {
				loginUserSession.setLoggedIn(true);
				loginUserSession.setLoggedInName(loginUserName);
				loginUserSession.setUserData(data.get());
				return true;
			} else {
				return false;
			}
		}
		
		return false;
	}
	
	@RequestMapping(value = {"/signup"}, method = RequestMethod.GET)
	public String signUp(Model model) {
		if (!loginUserSession.isLoggedIn()) {
			if (!model.containsAttribute("already")) {
				model.addAttribute("already", false);
			}
			return "signup";
		} else {
			model.addAttribute("loggedInName", loginUserSession.getLoggedInName());
			return "redirect:/index";
		}
	}
	
	@RequestMapping(value = {"/signup"}, method = RequestMethod.POST)
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.NESTED)
	public String signUpExec(Model model, @ModelAttribute("signUpForm") @Validated SignUpForm signUpForm, BindingResult result, RedirectAttributes attributes) {
		if (loginUserSession.isLoggedIn()) {
			return "redirect:/index";
		} else {
			String safeSignUpUsername = HtmlUtils.htmlEscape(signUpForm.getUsername());
			String safeSignUpPassword = HtmlUtils.htmlEscape(signUpForm.getPassword());
			Optional<UserData> searchedUserData = userDataRepository.findOneByName(safeSignUpUsername);
			if (searchedUserData.isEmpty() && !result.hasErrors()) {
				UserData userData = loginUserSession.getUserData();
				if (Objects.isNull(userData)) {
					userData = new UserData();
				}
				userData.setName(safeSignUpUsername);
				userData.setPasswordHash(passwordEncoder.encode(safeSignUpPassword));
				userData.setPlanDataSet(ConcurrentHashMap.newKeySet());
				userDataRepository.saveAndFlush(userData);
				if (loginAuth(safeSignUpUsername, safeSignUpPassword)) {
					return "redirect:/index";
				} else {
					return "redirect:/signup";
				}
			} else {
				attributes.addFlashAttribute("already", true);
				return "redirect:/signup";
			}
		}
	}
	
	@RequestMapping(value = {"/login"}, method = RequestMethod.GET)
	public String login(Model model) {
		if (!loginUserSession.isLoggedIn()) {
			return "login";
		} else {
			model.addAttribute("loggedInName", loginUserSession.getLoggedInName());
			return "redirect:/index";
		}
	}
	
	@RequestMapping(value = {"/login"}, method = RequestMethod.POST)
	@Transactional
	public String loginExec(Model model, @ModelAttribute("loginForm") @Validated LoginForm loginForm, BindingResult result) {
		String safeLoginUserName = HtmlUtils.htmlEscape(loginForm.getUsername());
		String safeLoginPassword = HtmlUtils.htmlEscape(loginForm.getPassword());
		if (!result.hasErrors()) {
			if (loginAuth(safeLoginUserName, safeLoginPassword)) {
				return "redirect:/index";
			} else {
				return "redirect:/login";
			}
		} else {
			return "redirect:/login";
		}
	}
	
	@RequestMapping(value = {"/add"}, method = RequestMethod.POST)
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.NESTED)
	public String addPlan(Model model, @ModelAttribute("addForm") @Validated AddForm addForm, BindingResult result, RedirectAttributes attributes) {
		if (!loginUserSession.isLoggedIn()) {
			return "redirect:/index";
		} else {
			String safeDate = HtmlUtils.htmlEscape(addForm.getDate());
			String safeName = HtmlUtils.htmlEscape(addForm.getName());
			if (!result.hasErrors()) {
				UserData userData = userDataRepository.findById(loginUserSession.getUserData().getId()).get();
				int year = Integer.parseInt(safeDate.substring(0, 4));
				int month = Integer.parseInt(safeDate.substring(5, 7));
				int day = Integer.parseInt(safeDate.substring(8, 10));
				LocalDateTime localDateTime = LocalDateTime.of(year, month, day, 0, 0);
				PlanData planData = new PlanData();
				planData.setLocalDateTime(localDateTime);
				planData.setName(safeName);
				planData.setExplanation("");
				planData.setPlace("");
				planData.setDuration(Duration.ZERO);
				planData.setUserData(userData);
				userData.getPlanDataSet().add(planData);
				planDataRepository.saveAndFlush(planData);
				userDataRepository.saveAndFlush(userData);
			}
		}
		
		return "redirect:/index";
	}
	
	@ModelAttribute("loginUserSession")
	@Bean
	public LoginUserSession startSession() {
		return new LoginUserSession();
	}
	
	@ModelAttribute
	@Bean
	public LoginForm loginForm() {
		return new LoginForm();
	}
	
	@ModelAttribute
	@Bean
	public SignUpForm signUpForm() {
		return new SignUpForm();
	}
	
	@ModelAttribute
	@Bean
	public AddForm addForm() {
		return new AddForm();
	}
	
	@Autowired
	public CalendarController(LoginUserSession loginUserSession, UserDataRepository userDataRepository, PlanDataRepository planDataRepository) {
		this.userDataRepository = userDataRepository;
		this.loginUserSession = loginUserSession;
		this.planDataRepository = planDataRepository;
		this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
