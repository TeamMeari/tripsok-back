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
@RedisHash(value = "refreshToken", timeToLive = 604800) // 기본 TTL을 604800초(7일)로 설정
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Indexed
	private String userId;
	private String token;

	public RefreshToken(String userId, String token) {
		this.userId = userId;
		this.token = token;
	}
}
