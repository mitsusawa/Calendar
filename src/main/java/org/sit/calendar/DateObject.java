package org.sit.calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sit.calendar.data.PlanData;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class DateObject {
	private int date;
	private DayOfWeek dayOfWeek;
	private Set<PlanData> planData;
	
	public DateObject(int date, DayOfWeek dayOfWeek) {
		this.date = date;
		this.dayOfWeek = dayOfWeek;
		this.planData = new ConcurrentHashMap<>().newKeySet();
	}
}
