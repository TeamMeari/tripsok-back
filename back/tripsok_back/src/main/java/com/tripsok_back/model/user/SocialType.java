package com.tripsok_back.model.user;

public enum SocialType {
	GOOGLE("구글"), EMAIL("이메일");

	private String description;

	SocialType(String description) {
		this.description = description;
	}
}
