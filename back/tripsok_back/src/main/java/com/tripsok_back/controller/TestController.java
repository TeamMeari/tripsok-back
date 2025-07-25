package com.tripsok_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.response.GetTestResponse;
import com.tripsok_back.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {
	private final TestService testService;

	@GetMapping()
	public ResponseEntity<GetTestResponse> testGet() {
		return ResponseEntity.ok().body(testService.getHello());
	}

	@GetMapping("/exception")
	public ResponseEntity<Void> test() {
		testService.getException();
		return ResponseEntity.noContent().build();
	}
}
