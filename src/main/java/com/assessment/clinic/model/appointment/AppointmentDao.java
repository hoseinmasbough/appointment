package com.assessment.clinic.model.appointment;

import java.util.List;

import com.assessment.clinic.model.doctor.Doctor;
import com.assessment.clinic.model.patient.Patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentDao extends JpaRepository<Appointment, Long> {

	List<Appointment> getAllByPatientAndStartTimeBetween(Patient patient, long from, long to);

	List<Appointment> getAllByDoctorAndStartTimeBetween(Doctor doctor, long from, long to);
}
