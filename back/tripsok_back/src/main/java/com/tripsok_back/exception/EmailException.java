package com.tripsok_back.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class EmailException extends CustomException {
	public EmailException(ErrorCode errorCode) {
		super(errorCode);
		log.error("EmailException occurred: {}", errorCode);
	}
}
