package com.tripsok_back.dto.email.request;

public record EmailVerifyRequest(String email, String code) {
}
