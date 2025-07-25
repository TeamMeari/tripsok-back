package com.tripsok_back.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tripsok_back.dto.user.UserSignUpRequest;

@Service
public class UserService {
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public void signUp(UserSignUpRequest request) {
		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		// TODO: 회원 정보 DB 저장 로직 추가(UserRepository 등)
		// 예시: userRepository.save(new User(request.getEmail(), encodedPassword, ...));
	}
}
