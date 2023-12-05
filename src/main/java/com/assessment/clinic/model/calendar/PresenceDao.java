package com.assessment.clinic.model.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresenceDao extends JpaRepository<Presence, Long> {

}
