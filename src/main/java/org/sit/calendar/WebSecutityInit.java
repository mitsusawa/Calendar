package org.sit.calendar;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;
import java.util.Set;

@Configuration
public class WebSecutityInit extends AbstractSecurityWebApplicationInitializer {
	@Override
	protected Set<SessionTrackingMode> getSessionTrackingModes() {
		return EnumSet.of(SessionTrackingMode.COOKIE);
	}
}