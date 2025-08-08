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
@RedisHash(value = "blackListAccessToken")
public class BlackListAccessToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Indexed
	private String token;
	@TimeToLive
	private Long expirationTime;

	public BlackListAccessToken(String token, Long expirationTime) {
		this.token = token;
		this.expirationTime = expirationTime;
	}
}
