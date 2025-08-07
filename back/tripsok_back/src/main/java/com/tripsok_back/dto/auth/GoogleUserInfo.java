package com.tripsok_back.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GoogleUserInfo {
	private String sub;
	private String email;
}
