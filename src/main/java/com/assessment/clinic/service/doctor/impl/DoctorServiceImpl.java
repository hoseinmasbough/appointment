package com.assessment.clinic.service.doctor.impl;

import com.assessment.clinic.exception.DoctorNotFoundException;
import com.assessment.clinic.model.doctor.Doctor;
import com.assessment.clinic.model.doctor.DoctorDao;
import com.assessment.clinic.service.doctor.DoctorService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

	private final DoctorDao dao;

	@Override
	public Doctor getDoctorById(Long id) throws DoctorNotFoundException {
		return dao.findById(id).orElseThrow(() -> new DoctorNotFoundException("no doctor found for id " + id));
	}
}
