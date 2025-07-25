package com.tripsok_back.security.jwt;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.tripsok_back.config.JwtProperties;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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

	public String generateAccessToken(String userId, Collection<? extends GrantedAuthority> authorities) {
		return Jwts.builder()
			.issuedAt(new Date())
			.expiration(
				Date.from(ZonedDateTime.now().plusMinutes(jwtProperties.getAccessTokenExpirationTime()).toInstant()))
			.claim("userId", userId)
			.claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).toList())
			.signWith(key)
			.compact();

	}

	public String validateAndExtract(String token) {
		try {
			return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().get("userId", String.class);
		} catch (ExpiredJwtException e) {
			throw new IllegalArgumentException("토큰이 만료되었습니다.");
		} catch (SignatureException e) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
		} catch (MalformedJwtException e) {
			throw new IllegalArgumentException("잘못된 형식의 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			throw new IllegalArgumentException("지원하지 않는 토큰입니다.");
		} catch (Exception e) {
			throw new IllegalArgumentException("토큰 검증 중 오류가 발생했습니다.");
		}
	}
}
