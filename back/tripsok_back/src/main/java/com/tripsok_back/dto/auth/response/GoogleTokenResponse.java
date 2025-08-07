package com.tripsok_back.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleTokenResponse {
	@JsonProperty("id_token")
	private String idToken;
}
