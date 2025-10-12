package com.tripsok_back.service.email;

import com.tripsok_back.dto.booking.request.BookingRequest;
import com.tripsok_back.dto.email.response.EmailVerifyResponse;
import com.tripsok_back.model.tripplan.TripPlan;

public interface EmailService {
	void sendVerificationEmail(String email);

	EmailVerifyResponse verifyEmailCode(String email, String code);

	void sendBookingConfirmationEmail(BookingRequest request, String userName, TripPlan tripPlan);
}
