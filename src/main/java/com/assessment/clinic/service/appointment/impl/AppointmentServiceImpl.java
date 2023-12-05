package com.assessment.clinic.service.appointment.impl;

import java.util.List;

import javax.validation.Valid;

import com.assessment.clinic.exception.BusinessException;
import com.assessment.clinic.exception.DoctorNotFoundException;
import com.assessment.clinic.exception.ValidationException;
import com.assessment.clinic.model.appointment.Appointment;
import com.assessment.clinic.model.appointment.AppointmentDao;
import com.assessment.clinic.model.calendar.Presence;
import com.assessment.clinic.model.doctor.Doctor;
import com.assessment.clinic.model.patient.Patient;
import com.assessment.clinic.service.appointment.AppointmentService;
import com.assessment.clinic.service.appointment.dto.CreateAutoAppointmentRequest;
import com.assessment.clinic.service.appointment.dto.CreateNewAppointmentRequest;
import com.assessment.clinic.service.appointment.mapper.AppointmentServiceMapper;
import com.assessment.clinic.service.doctor.DoctorService;
import com.assessment.clinic.service.patient.PatientService;
import com.assessment.clinic.utli.ConfigProvider;
import com.assessment.clinic.utli.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentValidator validator;

	private final DoctorService doctorService;

	private final PatientService patientService;

	private final AppointmentDao appointmentDao;

	private final AppointmentServiceMapper mapper;

	private final ConfigProvider configProvider;

	@Override
	public void setAppointment(@Valid CreateNewAppointmentRequest appointmentRequest) throws BusinessException {
		Doctor doctor = getDoctor(appointmentRequest.getDoctorId());
		validator.isValidTime(appointmentRequest.getAppointmentTime(), doctor);
		Patient patient = getPatient(appointmentRequest.getPatientId());
		List<Appointment> prevPatientAppointments = getAllPreviousAppointmentsByDateAndPatient(patient, appointmentRequest.getAppointmentTime());
		validator.isAppointmentValidForPatient(appointmentRequest.getAppointmentTime(), prevPatientAppointments, doctor, patient);
		List<Appointment> prevDoctorAppointments = getAllPreviousAppointmentsByDateAndDoctor(doctor, appointmentRequest.getAppointmentTime());
		validator.isAppointmentValidForDoctor(appointmentRequest.getAppointmentTime(), prevDoctorAppointments, doctor);
		save(appointmentRequest.getAppointmentTime(), doctor, patient);
	}

	@Override
	public void setEarliestAppointment(@Valid CreateAutoAppointmentRequest appointmentRequest) throws BusinessException {
		Doctor doctor = getDoctor(appointmentRequest.getDoctorId());
		Patient patient = getPatient(appointmentRequest.getPatientId());
		if (CollectionUtils.isEmpty(doctor.getPresences())) {
			throw new ValidationException("There is no doctor schedule for appointment");
		}
		for (Presence presence : doctor.getPresences()) {
			if (findAppointment(appointmentRequest, doctor, patient, presence)) {
				break;
			}
		}
	}

	private boolean findAppointment(CreateAutoAppointmentRequest appointmentRequest, Doctor doctor, Patient patient, Presence presence) {
		int windowTime = 0;
		long startTime = presence.getStartTime();
		long endTime = 0;
		List<Appointment> prevPatientAppointments = getAllPreviousAppointmentsByDateAndPatient(patient, startTime);
		List<Appointment> prevDoctorAppointments = getAllPreviousAppointmentsByDateAndDoctor(doctor, startTime);
		do {
			try {
				validator.isValidTime(startTime, doctor);
				validator.isAppointmentValidForPatient(startTime, prevPatientAppointments, doctor, patient);
				validator.isAppointmentValidForDoctor(startTime, prevDoctorAppointments, doctor);
				save(startTime, doctor, patient);
				return true;
			} catch (BusinessException exception) {
				log.debug("couldn't find time in windowTime {} and presence {}", windowTime, presence);
				windowTime++;
				startTime = startTime + (windowTime * appointmentRequest.getDurationInMin() * 60000L);
				endTime = startTime + (appointmentRequest.getDurationInMin() * 60000L);
			}
		} while (endTime <= presence.getEndTime());
		return false;
	}

	private List<Appointment> getAllPreviousAppointmentsByDateAndPatient(Patient patient, Long appointmentTime) {
		long startTimeOfDay = TimeUtil.getFirstMomentOfDay(appointmentTime);
		long endTimeOfDay = TimeUtil.getLastMomentOfDay(appointmentTime);
		return appointmentDao.getAllByPatientAndStartTimeBetween(patient, startTimeOfDay, endTimeOfDay);
	}

	private List<Appointment> getAllPreviousAppointmentsByDateAndDoctor(Doctor doctor, Long appointmentTime) {
		long startTimeOfDay = TimeUtil.getFirstMomentOfDay(appointmentTime);
		long endTimeOfDay = TimeUtil.getLastMomentOfDay(appointmentTime);
		return appointmentDao.getAllByDoctorAndStartTimeBetween(doctor, startTimeOfDay, endTimeOfDay);
	}

	private Doctor getDoctor(Long doctorId) throws DoctorNotFoundException {
		return doctorService.getDoctorById(doctorId);
	}

	private Patient getPatient(Long patientId) throws BusinessException {
		return patientService.getPatientById(patientId);
	}

	private void save(long time, Doctor doctor, Patient patient) {
		long appointmentDurationInMS = configProvider.getAppointmentDurationMinByDoctorGrade(doctor.getGrade()) * 60000L;
		long endAppointmentTime = time + appointmentDurationInMS;
		appointmentDao.save(mapper.toAppointment(doctor, patient, time, endAppointmentTime));
	}
}
