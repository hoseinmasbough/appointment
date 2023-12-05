package com.assessment.clinic.service.appointment.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.assessment.clinic.exception.BusinessException;
import com.assessment.clinic.exception.ValidationException;
import com.assessment.clinic.model.appointment.Appointment;
import com.assessment.clinic.model.calendar.PersianDayOfWeek;
import com.assessment.clinic.model.calendar.Presence;
import com.assessment.clinic.model.doctor.Doctor;
import com.assessment.clinic.model.patient.Patient;
import com.assessment.clinic.utli.ConfigProvider;
import com.assessment.clinic.utli.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentValidator {

	private final ConfigProvider configProvider;

	public void isAppointmentValidForDoctor(long startAppointmentTime, List<Appointment> prevDoctorAppointments, Doctor doctor) throws BusinessException {
		if (prevDoctorAppointments == null || prevDoctorAppointments.isEmpty()) {
			return;
		}
		long appointmentDurationInMS = configProvider.getAppointmentDurationMinByDoctorGrade(doctor.getGrade()) * 60000L;
		long endAppointmentTime = startAppointmentTime + appointmentDurationInMS;
		int maxDoctorConflictTime = configProvider.getValidConflictTimeCountByDoctorGrade(doctor.getGrade());
		int conflictTimeCount = 0;
		for (Appointment appointment : prevDoctorAppointments) {
			boolean startTimeHasConflict = startAppointmentTime >= appointment.getStartTime() && startAppointmentTime <= appointment.getEndTime();
			boolean endTimeHasConflict = endAppointmentTime >= appointment.getStartTime() && endAppointmentTime <= appointment.getEndTime();
			if (startTimeHasConflict || endTimeHasConflict) {
				conflictTimeCount++;
			}
			if (conflictTimeCount >= maxDoctorConflictTime) {
				log.debug("The allowed max conflict time count of doctor {} is exceeded: {}", doctor.getId(), conflictTimeCount);
				throw new ValidationException("The allowed max conflict time count of doctor is exceeded: " + conflictTimeCount);
			}
		}
	}

	public void isAppointmentValidForPatient(long startAppointmentTime, List<Appointment> prevPatientAppointments, Doctor doctor, Patient patient) throws BusinessException {
		if (prevPatientAppointments == null || prevPatientAppointments.isEmpty()) {
			return;
		}
		int maxAppointmentCount = configProvider.getMaxAppointmentCountByDay();
		if (prevPatientAppointments.size() >= maxAppointmentCount) {
			throw new ValidationException("The patient " + patient.getId() + " has previous appointments more than " + maxAppointmentCount);
		}
		long appointmentDurationInMS = configProvider.getAppointmentDurationMinByDoctorGrade(doctor.getGrade()) * 60000L;
		long endAppointmentTime = startAppointmentTime + appointmentDurationInMS;
		for (Appointment appointment : prevPatientAppointments) {
			boolean startTimeHasConflict = startAppointmentTime >= appointment.getStartTime() && startAppointmentTime <= appointment.getEndTime();
			boolean endTimeHasConflict = endAppointmentTime >= appointment.getStartTime() && endAppointmentTime <= appointment.getEndTime();
			if (startTimeHasConflict || endTimeHasConflict) {
				throw new ValidationException("The new appointment date has conflict with older appointment of patient " + appointment.getId());
			}
		}
	}

	public void isValidTime(long appointmentTime, Doctor doctor) throws BusinessException {
		if (appointmentTime == 0) {
			throw new ValidationException("Appointment time should greater than zero : " + appointmentTime);
		}
		LocalDateTime startAppointmentTime = TimeUtil.longToLocalDateTime(appointmentTime);
		LocalDateTime endAppointmentTime = startAppointmentTime.plusMinutes(configProvider.getAppointmentDurationMinByDoctorGrade(doctor.getGrade()));
		if (isInBusinessDay(startAppointmentTime)) {
			if (isInWorkingHours(startAppointmentTime, endAppointmentTime)) {
				if (!isInDoctorPresence(appointmentTime, TimeUtil.toEpochMilli(endAppointmentTime), doctor.getPresences())) {
					throw new ValidationException("Appointment time is not in doctor schedule");
				}
			} else {
				throw new ValidationException("Appointment hour is not in working hour : " + startAppointmentTime);
			}
		} else {
			throw new ValidationException("Appointment day is not in business day : " + startAppointmentTime);
		}

	}

	private boolean isInDoctorPresence(long startAppointmentTime, long endAppointmentTime, List<Presence> presences) {
		for (Presence presence : presences) {
			boolean startTimeInSchedule = startAppointmentTime >= presence.getStartTime() && startAppointmentTime < presence.getEndTime();
			boolean endTimeIsInSchedule = endAppointmentTime > presence.getStartTime() && endAppointmentTime <= presence.getEndTime();
			if (startTimeInSchedule && endTimeIsInSchedule) {
				return true;
			}
		}
		return false;
	}

	private boolean isInBusinessDay(LocalDateTime startAppointmentTime) {
		String day = startAppointmentTime.getDayOfWeek().toString();
		PersianDayOfWeek startDay = PersianDayOfWeek.fromValue(configProvider.getStartDayOfWeek());
		PersianDayOfWeek endDay = PersianDayOfWeek.fromValue(configProvider.getEndDayOfWeek());
		PersianDayOfWeek appointmentDay = PersianDayOfWeek.fromValue(day);
		return appointmentDay.getValue() >= startDay.getValue() && appointmentDay.getValue() <= endDay.getValue();
	}

	private boolean isInWorkingHours(LocalDateTime start, LocalDateTime end) {
		int startHour = start.getHour();
		int endHour = end.getHour();
		int endMinutes = end.getMinute();
		if (startHour >= configProvider.getStartWorkingHour() && endHour < configProvider.getEndWorkingHour()) {
			return true;
		}
		return endHour == configProvider.getEndWorkingHour() && endMinutes == 0;
	}
}
