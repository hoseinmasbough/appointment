package com.assessment.clinic.service.appointment.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateAutoAppointmentRequest {
	@NotNull(message = "doctor is mandatory")
	private Long doctorId;

	@NotNull(message = "patient is mandatory")
	private Long patientId;

	@NotNull
	@Min(value = 5, message = "duration is greater than 5")
	@Max(value = 30, message = "duration is less than 30")
	private Integer durationInMin;
}
