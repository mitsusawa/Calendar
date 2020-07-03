package org.sit.calendar.data;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class PlanData implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, unique = true)
	private Long id;
	
	@Column(nullable = false, unique = false)
	@NotEmpty
	private String name;
	
	@Column(nullable = true)
	private String explanation;
	
	@Column(nullable = true)
	private String place;
	
	@Column(nullable = false)
	private LocalDateTime localDateTime;
	
	@Column(nullable = false)
	private Duration duration;
	
	@ManyToOne(targetEntity = UserData.class, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
	@NotFound(action = NotFoundAction.IGNORE)
	private UserData userData;
}
