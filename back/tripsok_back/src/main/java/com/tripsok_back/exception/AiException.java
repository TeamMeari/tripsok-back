package com.tripsok_back.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AiException extends CustomInternalException {
	public AiException(InternalErrorCode message, String errorMessage) {
		super(message);
		log.error("AiException occurred: {}, cause: {}", message, errorMessage);
	}
}
