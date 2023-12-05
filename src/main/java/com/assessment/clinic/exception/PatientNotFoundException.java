package com.assessment.clinic.exception;

public class PatientNotFoundException extends BusinessException {
	public PatientNotFoundException(String message) {
		super(message);
	}

	@Override
	public ResultStatus getResultStatus() {
		return ResultStatus.PATIENT_NOT_FOUND;
	}
}
