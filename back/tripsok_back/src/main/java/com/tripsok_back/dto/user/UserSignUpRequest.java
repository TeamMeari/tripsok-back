package com.tripsok_back.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSignUpRequest {
	private String email;
	private String password;
	private String name;
	private String socialType;
}
