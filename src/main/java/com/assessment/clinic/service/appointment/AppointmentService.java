package com.assessment.clinic.service.appointment;

import com.assessment.clinic.exception.BusinessException;
import com.assessment.clinic.service.appointment.dto.CreateAutoAppointmentRequest;
import com.assessment.clinic.service.appointment.dto.CreateNewAppointmentRequest;

public interface AppointmentService {

	void setAppointment(CreateNewAppointmentRequest appointmentRequest) throws BusinessException;

	void setEarliestAppointment(CreateAutoAppointmentRequest appointmentRequest) throws BusinessException;
}
