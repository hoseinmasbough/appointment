package com.assessment.clinic.utli;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

	public static LocalDateTime longToLocalDateTime(long timestamp) {
		if (timestamp == 0) {
			return null;
		}
		return Instant.ofEpochMilli(timestamp)
				.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static long toEpochMilli(LocalDateTime time) {
		return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static Long getLastMomentOfDay(Long date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(date);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
		return calendar.getTimeInMillis();
	}

	public static Long getFirstMomentOfDay(Long date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
}
