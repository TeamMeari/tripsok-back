package com.tripsok_back.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AiException extends CustomInternalException {
	public AiException(InternalErrorCode message, Throwable cause) {
		super(message);
		log.error("AiException occurred: {}, cause: {}", message, cause.getMessage());
	}
}
