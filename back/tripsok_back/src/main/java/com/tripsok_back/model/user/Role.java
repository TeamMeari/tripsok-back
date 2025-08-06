package com.tripsok_back.model.user;

import java.util.List;

import lombok.Getter;

@Getter
public enum Role {
	USER(List.of("ROLE_USER")),
	ADMIN(List.of("ROLE_USER", "ROLE_ADMIN"));

	private final List<String> authority;

	Role(List<String> authority) {
		this.authority = authority;
	}
}
