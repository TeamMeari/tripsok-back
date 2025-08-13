package com.tripsok_back.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class AuthException extends CustomException {
	public AuthException(ErrorCode errorCode) {
		super(errorCode);
		log.error("AuthException occurred: {}", errorCode);
	}

	public AuthException(ErrorCode errorCode, String message) {
		super(errorCode, message);
		log.error("AuthException occurred: {}, message: {}", errorCode, message);
	}
}
