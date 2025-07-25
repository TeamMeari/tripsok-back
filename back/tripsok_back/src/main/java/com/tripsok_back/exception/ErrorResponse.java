package com.tripsok_back.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
	int code;
	String errorMessage;

	public ErrorResponse(int code, String errorMessage) {
		this.code = code;
		this.errorMessage = errorMessage;
	}
}
