package com.tripsok_back.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class PlaceException extends CustomException {
	public PlaceException(ErrorCode errorCode) {
		super(errorCode);
		log.error("PlaceException occurred: {}", errorCode);
	}

}
