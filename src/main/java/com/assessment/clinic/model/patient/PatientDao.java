package com.assessment.clinic.model.patient;

import com.assessment.clinic.model.doctor.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientDao extends JpaRepository<Patient, Long> {

}
