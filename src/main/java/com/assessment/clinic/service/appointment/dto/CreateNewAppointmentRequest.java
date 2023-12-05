package com.assessment.clinic.service.appointment.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class CreateNewAppointmentRequest extends CreateAutoAppointmentRequest {

	@NotNull
	@Positive(message = "time is not negative")
	private Long appointmentTime;
}
