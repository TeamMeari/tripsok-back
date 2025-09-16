package com.tripsok_back.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ServiceBlockException extends CustomInternalException {
	public ServiceBlockException(InternalErrorCode errorCode) {
		super(InternalErrorCode.SERVICE_REQUEST_LIMIT_EXCEEDED, errorCode.getErrorMessage());
	}

	public ServiceBlockException() {
		super(InternalErrorCode.SERVICE_REQUEST_LIMIT_EXCEEDED);
	}
}
