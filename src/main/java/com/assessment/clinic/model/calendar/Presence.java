package com.assessment.clinic.model.calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.assessment.clinic.model.doctor.Doctor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doctor_presences")
@Setter
@Getter
public class Presence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Doctor doctor;

	private Long startTime;

	private Long endTime;
}
