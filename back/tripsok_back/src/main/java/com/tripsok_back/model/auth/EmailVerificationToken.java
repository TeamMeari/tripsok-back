package com.tripsok_back.model.auth;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@RedisHash(value = "emailVerification")
public class EmailVerificationToken {
	@Id
	private Integer id;

	@Indexed
	private String email;
	private String verificationCode;
	@TimeToLive
	private Long expirationTime;

	public EmailVerificationToken(String email, String verificationCode, Long expirationTime) {
		this.email = email;
		this.verificationCode = verificationCode;
		this.expirationTime = expirationTime;
	}
}
