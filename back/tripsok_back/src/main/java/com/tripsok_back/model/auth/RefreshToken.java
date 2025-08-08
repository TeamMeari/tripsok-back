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
@RedisHash(value = "refreshToken")
public class RefreshToken {
	@Id
	private Integer id;

	@Indexed
	private Integer userId;
	private String token;
	@TimeToLive
	private Long expirationTime;

	public RefreshToken(Integer userId, String token, Long expirationTime) {
		this.userId = userId;
		this.token = token;
		this.expirationTime = expirationTime;
	}
}
