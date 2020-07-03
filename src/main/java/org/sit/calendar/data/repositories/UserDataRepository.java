package org.sit.calendar.data.repositories;

import org.sit.calendar.data.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
	public Optional<UserData> findById(Long id);
	public Set<UserData> findByIdIsNotNull();
	public Optional<UserData> findOneByName(String name);
}
