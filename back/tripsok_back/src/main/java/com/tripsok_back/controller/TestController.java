package com.tripsok_back.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.service.auth.AuthService;
import com.tripsok_back.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
	private final AuthService authService;
	private final UserService userService;

	@GetMapping("/user/{userId}")
	public String getUserAccessToken(
		@PathVariable Integer userId
	) {
		return authService.getUserAccessToken(userId);
	}

	@GetMapping("/user/list")
	public List<TripSokUser> getUserList() {
		return userService.findAllUser();
	}

	@DeleteMapping("/user/{userId}")
	public void deleteUser(
		@PathVariable Integer userId
	) {
		userService.deleteUser(userId);
	}
}
