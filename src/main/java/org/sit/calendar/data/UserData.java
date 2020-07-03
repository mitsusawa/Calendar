package org.sit.calendar.data;

import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
public class UserData implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, unique = true)
	private Long id;
	
	@Column(nullable = false, unique = true)
	@NotEmpty
	private String name;
	
	@Column(nullable = false, unique = true)
	@NotEmpty
	private String passwordHash;
	
	@OneToMany(targetEntity = PlanData.class, mappedBy = "userData", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
	@NotFound(action = NotFoundAction.IGNORE)
	@Column(nullable = true)
	@Fetch(FetchMode.SUBSELECT)
	private Set<PlanData> planDataSet;
}
