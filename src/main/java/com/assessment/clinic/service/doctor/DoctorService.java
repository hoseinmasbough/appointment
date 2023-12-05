package com.assessment.clinic.service.doctor;

import com.assessment.clinic.exception.DoctorNotFoundException;
import com.assessment.clinic.model.doctor.Doctor;

public interface DoctorService {

	Doctor getDoctorById(Long id) throws DoctorNotFoundException;
}
