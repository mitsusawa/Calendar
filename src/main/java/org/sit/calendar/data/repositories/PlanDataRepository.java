package org.sit.calendar.data.repositories;

import org.sit.calendar.data.PlanData;
import org.sit.calendar.data.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PlanDataRepository extends JpaRepository<PlanData, Long> {
	public Optional<PlanData> findById(Long id);
}
