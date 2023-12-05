package com.assessment.clinic.exception;

public class ValidationException extends BusinessException {
	public ValidationException(String message) {
		super(message);
	}

	@Override
	public ResultStatus getResultStatus() {
		return ResultStatus.VALIDATION_FAILED;
	}
}
