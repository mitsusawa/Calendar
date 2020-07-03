package org.sit.calendar.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginForm {
	@NotEmpty
	@Size(min = 2, max = 32)
	private String username;
	
	@NotEmpty
	@Size(min = 4, max = 32)
	private String password;
}
