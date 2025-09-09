package com.tripsok_back.service.email;

import com.tripsok_back.dto.email.response.EmailVerifyResponse;

public interface EmailService {
	void sendVerificationEmail(String email);

	EmailVerifyResponse verifyEmailCode(String email, String code);
}
