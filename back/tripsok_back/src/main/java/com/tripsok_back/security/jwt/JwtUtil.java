package com.tripsok_back.security.jwt;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.tripsok_back.config.JwtProperties;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.exception.JwtException;
import com.tripsok_back.model.user.SocialType;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtUtil {
	private final JwtProperties jwtProperties;
	private final SecretKey key;

	public JwtUtil(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		if (jwtProperties.getSecretKey() == null || jwtProperties.getSecretKey().isEmpty()) {
			log.error("JWT 시크릿 키가 설정되지 않았습니다.");
			throw new IllegalArgumentException("JWT 시크릿 키는 반드시 설정되어야 합니다.");
		}
		this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
	}

	public String generateAccessToken(Integer userId, Collection<GrantedAuthority> authorities) {
		Map<String, Object> claims = Map.of(
			"authorities", authorities.stream().map(GrantedAuthority::getAuthority).toList(),
			"userId", userId
		);
		return generateToken(claims, jwtProperties.getAccessTokenExpirationTime());
	}

	public String generateRefreshToken(Integer userId) {
		Map<String, Object> claims = Map.of(
			"userId", userId
		);
		return generateToken(claims, jwtProperties.getRefreshTokenExpirationTime());
	}

	public String generateEmailVerificationToken(String email) {
		Map<String, Object> claims = Map.of(
			"email", email
		);
		return generateToken(claims, jwtProperties.getEmailVerificationTokenExpirationTime());
	}

	public String generateOAuth2Token(String authAccessToken, SocialType socialType) {
		Map<String, Object> claims = Map.of(
			"authAccessToken", authAccessToken,
			"socialType", socialType.name()
		);
		return generateToken(claims, jwtProperties.getOauth2AccessTokenExpirationTime());
	}

	private String generateToken(Map<String, Object> claims, long expirationMinutes) {
		return Jwts.builder()
			.issuedAt(new Date())
			.expiration(Date.from(ZonedDateTime.now().plusSeconds(expirationMinutes).toInstant()))
			.claims(claims)
			.signWith(key)
			.compact();
	}

	public <T> T validateAndExtract(String token, String claimName, Class<T> targetType) {
		try {
			T content = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get(claimName, targetType);
			if (content == null) {
				throw new JwtException(ErrorCode.INVALID_TOKEN);
			}
			return content;
		} catch (ExpiredJwtException e) {
			throw new JwtException(ErrorCode.TOKEN_EXPIRED);
		} catch (Exception e) {
			throw new JwtException(ErrorCode.INVALID_TOKEN);
		}
	}

	public List<GrantedAuthority> getAuthorities(String token) {
		return validateAndExtract(token, "authorities", List.class)
			.stream()
			.map(authority -> new SimpleGrantedAuthority((String)authority))
			.toList();
	}

	public long getRefreshTokenExpirationTime() {
		return jwtProperties.getRefreshTokenExpirationTime();
	}

	public long getEmailVerificationTokenExpirationTime() {
		return jwtProperties.getEmailVerificationTokenExpirationTime();
	}
}
