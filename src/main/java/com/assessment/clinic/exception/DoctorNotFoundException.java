package com.assessment.clinic.exception;

public class DoctorNotFoundException extends BusinessException {
	public DoctorNotFoundException(String message) {
		super(message);
	}

	@Override
	public ResultStatus getResultStatus() {
		return ResultStatus.DOCTOR_NOT_FOUND;
	}
}
