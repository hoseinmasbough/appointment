package com.assessment.clinic.service.patient.impl;

import com.assessment.clinic.exception.PatientNotFoundException;
import com.assessment.clinic.model.patient.Patient;
import com.assessment.clinic.model.patient.PatientDao;
import com.assessment.clinic.service.patient.PatientService;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

	private final PatientDao dao;

	@Override
	public Patient getPatientById(long id) throws PatientNotFoundException {
		return dao.findById(id).orElseThrow(() -> new PatientNotFoundException("no patient found for id " + id));
	}
}
