package org.sit.calendar.session;

import lombok.Getter;
import lombok.Setter;
import org.sit.calendar.data.UserData;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.Objects;

@SessionScope
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class LoginUserSession implements Serializable {
	private UserData userData;
	private boolean loggedIn;
	private String loggedInName;
	
	public LoginUserSession() {
		this.loggedIn = false;
		this.loggedInName = "";
	}
	
	public LoginUserSession getLoginUserSession() {
		if (Objects.nonNull(this)) {
			return this;
		} else {
			return new LoginUserSession();
		}
	}
}
