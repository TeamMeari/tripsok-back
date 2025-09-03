package com.tripsok_back.service.email;

import static com.tripsok_back.exception.ErrorCode.*;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.tripsok_back.dto.email.response.EmailVerifyResponse;
import com.tripsok_back.exception.EmailException;
import com.tripsok_back.model.auth.EmailVerificationToken;
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
		String subject = "[TripSok] 이메일 인증 코드 안내";
		try {
			EmailVerificationToken codeFoundByEmail = emailVerificationTokenRepository.findByEmail(email);
			if (codeFoundByEmail != null) {
				emailVerificationTokenRepository.delete(codeFoundByEmail);
			}
			MimeMessage message = createEmailMessage(email, subject, code);
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

	private String createCode() {
		int codeLength = 6;
		Random random = new SecureRandom();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < codeLength; i++) {
			code.append(random.nextInt(10));
		}
		return code.toString();
	}

	private MimeMessage createEmailMessage(String email, String subject, String code) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		Context context = new Context();
		context.setVariable("code", code);
		String htmlContent = templateEngine.process("email-verify-template", context);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);
		helper.setTo(email);
		return message;
	}
}
