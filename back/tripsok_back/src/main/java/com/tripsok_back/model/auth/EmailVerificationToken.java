package com.tripsok_back.model.auth;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@RedisHash(value = "emailVerification", timeToLive = 600) // 기본 TTL을 600초(10분)로 설정
public class EmailVerificationToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Indexed
	private String email;
	private String verificationCode;

	public EmailVerificationToken(String email, String verificationCode) {
		this.email = email;
		this.verificationCode = verificationCode;
	}
}
