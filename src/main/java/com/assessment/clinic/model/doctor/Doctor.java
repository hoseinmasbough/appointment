package com.assessment.clinic.model.doctor;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.assessment.clinic.model.calendar.Presence;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doctors")
@Setter
@Getter
public class Doctor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "grade_type")
	private DoctorGrade grade;

	@OneToMany(
			mappedBy = "doctor",
			cascade = CascadeType.ALL,
			orphanRemoval = true
	)
	private List<Presence> presences = new ArrayList<>();

}
