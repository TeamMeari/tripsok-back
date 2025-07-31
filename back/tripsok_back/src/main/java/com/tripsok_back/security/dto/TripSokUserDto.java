package com.tripsok_back.security.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.tripsok_back.model.user.SocialType;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
public class TripSokUserDto extends User {
	private String userId;
	private String name;
	private SocialType socialType;

	public TripSokUserDto(String userId, String password, SocialType socialType,
		Collection<GrantedAuthority> authorities) {
		super(userId, password, authorities);
		this.userId = userId;
		this.socialType = socialType;
		log.info("TripSokUserDto created: {}", this);
	}
}
