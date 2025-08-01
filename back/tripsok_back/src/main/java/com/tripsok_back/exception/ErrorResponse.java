package com.tripsok_back.exception;

public record ErrorResponse(int code, String errorMessage) {
}
