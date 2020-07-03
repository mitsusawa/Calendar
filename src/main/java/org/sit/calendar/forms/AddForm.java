package org.sit.calendar.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class AddForm {
	@NotEmpty
	private String date;
	
	@NotEmpty
	private String name;
}
