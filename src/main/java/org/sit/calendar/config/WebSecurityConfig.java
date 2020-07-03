package org.sit.calendar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/", "/js/*", "/css/*", "/img/*", "/locales/*", "/*", "/admin/*").permitAll()
				.anyRequest().authenticated();
		http.antMatcher("/js/*").headers().frameOptions().sameOrigin();
		http.antMatcher("/css/*").headers().frameOptions().sameOrigin();
	}
}