package com.tripsok_back.service;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.response.GetTestResponse;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.exception.TestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {

	public GetTestResponse getHello() {
		return new GetTestResponse("Hello, World!","Tripsok");
	}

	public void getException() {
		throw new TestException(ErrorCode.BAD_REQUEST_JSON);
	}
}
