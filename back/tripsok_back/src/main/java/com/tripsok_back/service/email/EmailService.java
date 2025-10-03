package com.tripsok_back.service.email;

import com.tripsok_back.dto.email.response.EmailVerifyResponse;
import com.tripsok_back.model.tripplan.TripPlan;

public interface EmailService {
	void sendVerificationEmail(String email);

	EmailVerifyResponse verifyEmailCode(String email, String code);

	void sendBookingConfirmationEmail(String email, String userName, TripPlan tripPlan);
}
