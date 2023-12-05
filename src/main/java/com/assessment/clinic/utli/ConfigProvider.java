package com.assessment.clinic.utli;

import com.assessment.clinic.model.doctor.DoctorGrade;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfigProvider {

	private final Environment environment;

	public ConfigProvider(final Environment environment) {
		this.environment = environment;
	}

	public String getStartDayOfWeek() {
		return this.environment.getRequiredProperty("start.day.of.week", String.class);
	}

	public String getEndDayOfWeek() {
		return this.environment.getRequiredProperty("end.day.of.week", String.class);
	}

	public int getStartWorkingHour() {
		return this.environment.getRequiredProperty("start.hour.of.visit", Integer.class);
	}

	public int getEndWorkingHour() {
		return this.environment.getRequiredProperty("end.hour.of.visit", Integer.class);
	}

	public int getMaxAppointmentCountByDay() {
		return this.environment.getRequiredProperty("max.appointment.count.in.day", Integer.class);
	}

	public int getAppointmentDurationMinByDoctorGrade(DoctorGrade doctorGrade){
		return this.environment.getRequiredProperty(doctorGrade.name().toLowerCase() + ".visit.duration.in.min", Integer.class);
	}

	public int getValidConflictTimeCountByDoctorGrade(DoctorGrade doctorGrade){
		return this.environment.getRequiredProperty(doctorGrade.name().toLowerCase() + ".max.conflict.time.count", Integer.class);
	}
}
