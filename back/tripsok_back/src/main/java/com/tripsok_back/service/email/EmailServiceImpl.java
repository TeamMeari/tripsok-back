package com.tripsok_back.service.email;

import static com.tripsok_back.exception.ErrorCode.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.tripsok_back.dto.booking.request.BookingRequest;
import com.tripsok_back.dto.email.response.EmailVerifyResponse;
import com.tripsok_back.exception.EmailException;
import com.tripsok_back.model.auth.EmailVerificationToken;
import com.tripsok_back.model.tripplan.TripPlan;
import com.tripsok_back.repository.email.RedisEmailVerificationTokenRepository;
import com.tripsok_back.security.jwt.JwtUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
	private final RedisEmailVerificationTokenRepository emailVerificationTokenRepository;
	private final JwtUtil jwtUtil;

	@Override
	@Transactional
	public void sendVerificationEmail(String email) {
		String code = createCode();
		HashMap<String, Object> values = new HashMap<>();
		values.put("code", code);
		try {
			EmailVerificationToken codeFoundByEmail = emailVerificationTokenRepository.findByEmail(email);
			if (codeFoundByEmail != null) {
				emailVerificationTokenRepository.delete(codeFoundByEmail);
			}
			MimeMessage message = createEmailMessage(email, "[Tourang] Email Verification Code", values,
				"email-verify-template");
			emailVerificationTokenRepository.save(
				new EmailVerificationToken(email, code, jwtUtil.getEmailVerificationTokenExpirationTime()));
			mailSender.send(message);
			// TODO: 비동기 처리 고려
			log.info("이메일 전송 완료: {}, 인증코드: {}", email, code);
		} catch (Exception e) {
			log.error("이메일 전송 실패: {}", e.getMessage());
			throw new EmailException(EMAIL_SEND_FAILED);
		}
	}

	@Override
	@Transactional
	public EmailVerifyResponse verifyEmailCode(String email, String code) {
		EmailVerificationToken codeFoundByEmail = emailVerificationTokenRepository.findByEmail(email);
		if (codeFoundByEmail == null || !codeFoundByEmail.getVerificationCode().equals(code)) {
			throw new EmailException(EMAIL_VERIFICATION_CODE_INVALID);
		}
		emailVerificationTokenRepository.delete(codeFoundByEmail);
		log.info("이메일 인증 성공: {}, 인증코드: {}", email, code);
		return new EmailVerifyResponse(jwtUtil.generateEmailVerificationToken(email));
	}

	@Override
	public void sendBookingConfirmationEmail(BookingRequest request, String userName, TripPlan tripPlan) {
		try {
			HashMap<String, Object> values = new HashMap<>();
			String email = request.getContactEmail();
			values.put("userName", userName);
			values.put("tripDate", tripPlan.getTripDate().toString());
			values.put("startTime", tripPlan.getStartTime().toString());
			values.put("numberOfParticipants", tripPlan.getNumberOfPeople());
			values.put("amount", request.getPaymentInfo().getAmount());
			MimeMessage message = createEmailMessage(email, "[Tourang] Your Payment Has Been Confirmed", values,
				"payment_confirmation_template");
			mailSender.send(message);
			log.info("이메일 전송 완료: {}", email);
		} catch (Exception e) {
			log.error("이메일 전송 실패: {}", e.getMessage());
			throw new EmailException(EMAIL_SEND_FAILED);
		}
	}

	private String createCode() {
		int codeLength = 6;
		Random random = new SecureRandom();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < codeLength; i++) {
			code.append(random.nextInt(10));
		}
		return code.toString();
	}

	private MimeMessage createEmailMessage(String email, String subject, HashMap<String, Object> values,
		String template) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		Context context = new Context();
		for (String key : values.keySet()) {
			context.setVariable(key, values.get(key));
		}
		String htmlContent = templateEngine.process(template, context);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);
		helper.setTo(email);
		return message;
	}
}
