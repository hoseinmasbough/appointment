package com.assessment.clinic.exception;

public abstract class BusinessException extends Exception {
	private static final long serialVersionUID = -3749766539158141005L;

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public abstract ResultStatus getResultStatus();
}
