package com.tripsok_back.exception;

import lombok.Getter;

@Getter
public class CustomInternalException extends RuntimeException {
	private final InternalErrorCode errorCode;

	public CustomInternalException(InternalErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}

	public CustomInternalException(InternalErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
}
