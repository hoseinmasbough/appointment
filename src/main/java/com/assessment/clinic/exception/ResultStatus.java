package com.assessment.clinic.exception;

import java.io.IOException;
import java.util.Properties;

public enum ResultStatus {
	DOCTOR_NOT_FOUND(1, "doctor.not.found"),
	VALIDATION_FAILED(2, "validation.failed"),
	PATIENT_NOT_FOUND(3, "patient.not.found");

	private final String description;
	private final Integer statusCode;

	private ResultStatus(int statusCode, String description) {
		this.statusCode = statusCode;
		String errorMsg = ResultStatus.MessageHolder.ERROR_MESSAGE_PROPERTIES.getProperty(description);
		this.description = errorMsg != null ? errorMsg : description;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getStatusCode() {
		return this.statusCode;
	}

	private static final class MessageHolder {
		private static final Properties ERROR_MESSAGE_PROPERTIES = new Properties();

		private MessageHolder() {
		}

		static {
			try {
				ERROR_MESSAGE_PROPERTIES.load(ResultStatus.class.getResourceAsStream("/error-messages.properties"));
			} catch (IOException var1) {
				throw new ExceptionInInitializerError(var1);
			}
		}
	}
}
