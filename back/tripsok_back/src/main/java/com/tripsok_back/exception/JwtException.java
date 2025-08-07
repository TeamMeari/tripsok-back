package com.tripsok_back.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtException extends CustomException {
	public JwtException(ErrorCode errorCode) {
		super(errorCode);
		log.error("JwtException occurred: {}", errorCode);
	}
}
