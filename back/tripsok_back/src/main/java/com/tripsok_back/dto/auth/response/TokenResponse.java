package com.tripsok_back.dto.auth.response;

public record TokenResponse(String accessToken, String refreshToken) {
}
