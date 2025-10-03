package com.tripsok_back.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TripPlanException extends CustomException {
	public TripPlanException(ErrorCode errorCode) {
		super(errorCode);
		log.error("TripPlanException occurred: {}", errorCode);
	}

	public TripPlanException(ErrorCode errorCode, String message) {
		super(errorCode, message);
		log.error("TripPlanException occurred: {}, message: {}", errorCode, message);
	}
}
