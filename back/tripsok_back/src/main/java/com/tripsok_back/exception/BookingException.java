package com.tripsok_back.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingException extends CustomException {
	public BookingException(ErrorCode errorCode) {
		super(errorCode);
		log.error("BookingException occurred: {}", errorCode);
	}

	public BookingException(ErrorCode errorCode, String message) {
		super(errorCode, message);
		log.error("BookingException occurred: {}, message: {}", errorCode, message);
	}
}
