package com.tripsok_back.exception;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestException extends CustomException {
	public TestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
