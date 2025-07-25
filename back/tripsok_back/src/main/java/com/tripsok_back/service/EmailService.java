package com.tripsok_back.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	// 실제로는 DB나 캐시에서 인증코드 검증해야 함
	public boolean verifyEmailCode(String email, String code) {
		// TODO: 이메일 인증 코드 검증 로직 구현
		// 예시: 인증코드가 "123456"이면 성공
		return "123456".equals(code);
	}
}
