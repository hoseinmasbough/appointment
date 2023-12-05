package com.assessment.clinic.service.patient;

import com.assessment.clinic.exception.PatientNotFoundException;
import com.assessment.clinic.model.patient.Patient;

public interface PatientService {

	Patient getPatientById(long id) throws PatientNotFoundException;
}
