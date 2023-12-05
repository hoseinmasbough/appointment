package com.assessment.clinic.service.appointment.mapper;

import com.assessment.clinic.model.appointment.Appointment;
import com.assessment.clinic.model.doctor.Doctor;
import com.assessment.clinic.model.patient.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppointmentServiceMapper {

	@Mapping(target = "id", ignore = true)
	Appointment toAppointment(Doctor doctor, Patient patient, Long startTime, Long endTime);
}
