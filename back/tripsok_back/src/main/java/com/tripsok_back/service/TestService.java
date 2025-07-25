package com.tripsok_back.service;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.response.GetTestResponse;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.exception.TestException;
import com.tripsok_back.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
	private final UserRepository userRepository;

	public GetTestResponse getHello() {
		return new GetTestResponse("Hello, World!","Tripsok");
	}

	public void getException() {
		throw new TestException(ErrorCode.BAD_REQUEST_JSON);
	}
}
