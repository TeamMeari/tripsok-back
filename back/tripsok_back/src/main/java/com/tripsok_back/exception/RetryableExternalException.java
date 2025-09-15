package com.tripsok_back.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class RetryableExternalException extends CustomInternalException {
	public RetryableExternalException(InternalErrorCode errorCode) {
		super(InternalErrorCode.RETRYABLE_EXTERNAL_ERROR, errorCode.getErrorMessage());
	}
}
