package com.tripsok_back.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class UserException extends CustomException {
	public UserException(ErrorCode errorCode) {
		super(errorCode);
		log.error("UserException occurred: {}", errorCode);
	}

}
