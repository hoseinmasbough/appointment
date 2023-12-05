package com.assessment.clinic.model.calendar;


import java.security.InvalidParameterException;
import java.util.stream.Stream;

public enum PersianDayOfWeek {
	SATURDAY(1),
	SUNDAY(2),
	MONDAY(3),
	TUESDAY(4),
	WEDNESDAY(5),
	THURSDAY(6),
	FRIDAY(7);

	private int value;

	PersianDayOfWeek(int value) {
		this.value = value;
	}

	public static PersianDayOfWeek fromValue(String dayName) {
		return Stream.of(PersianDayOfWeek.values()).filter(day -> day.name().equalsIgnoreCase(dayName)).findFirst()
				.orElseThrow(() -> new InvalidParameterException("invalid day found " + dayName));
	}

	public int getValue() {
		return value;
	}
}
