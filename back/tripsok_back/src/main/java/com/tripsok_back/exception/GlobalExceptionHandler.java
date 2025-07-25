package com.tripsok_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 사용자 정의 예외 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.error("CustomException: {}", e.getMessage());
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getErrorMessage());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}

	// 일반적인 RuntimeException 처리
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		log.error("RuntimeException: {}", e.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(-1, e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	// Validation 실패 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
		log.error("Validation error: {}", e.getMessage());
		String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		ErrorResponse errorResponse = new ErrorResponse(-2, errorMessage);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	// 타입 불일치 예외 처리
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
		log.error("Type mismatch error: {}", e.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(-3, "잘못된 파라미터 타입입니다.");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	// 모든 예외에 대한 기본 처리
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("Unexpected error: {}", e.getMessage(), e);
		ErrorResponse errorResponse = new ErrorResponse(-999, "서버 내부 오류가 발생했습니다.");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
