package com.assessment.clinic.model.appointment;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.assessment.clinic.model.doctor.Doctor;
import com.assessment.clinic.model.patient.Patient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Setter
@Getter
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Doctor doctor;

	@ManyToOne(fetch = FetchType.LAZY)
	private Patient patient;

	private Long StartTime;

	private Long endTime;
}
