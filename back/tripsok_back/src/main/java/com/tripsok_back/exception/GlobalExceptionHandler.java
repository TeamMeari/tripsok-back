package com.tripsok_back.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	// Convertor 에서 바인딩 실패시 발생하는 예외
	@ExceptionHandler(value = {HttpMessageNotReadableException.class})
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		log.error("HttpMessageNotReadableException : {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.BAD_REQUEST_JSON;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getErrorMessage());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}
	// 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
	@ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
	public ResponseEntity<ErrorResponse> handleNoPageFoundException(Exception e) {
		log.error("NoHandlerFoundException : {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.NOT_FOUND_END_POINT;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getErrorMessage());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}

	// @Validated 어노테이션을 사용하여 검증을 수행할 때 발생하는 예외
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorResponse> handleArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException : {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), e.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}

	// 메소드의 인자 타입이 일치하지 않을 때 발생하는 예외
	@ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ErrorResponse> handleArgumentNotValidException(MethodArgumentTypeMismatchException e) {
		log.error("MethodArgumentTypeMismatchException : {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.INVALID_TYPE_VALUE;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getErrorMessage());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}

	// 필수 파라미터가 누락되었을 때 발생하는 예외
	@ExceptionHandler(value = {MissingServletRequestParameterException.class})
	public ResponseEntity<ErrorResponse> handleArgumentNotValidException(MissingServletRequestParameterException e) {
		log.error("MissingServletRequestParameterException : {}", e.getMessage());
		ErrorCode errorCode = ErrorCode.MISSING_REQUEST_PARAMETER;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), e.getMessage());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.error("CustomException: {}", e.getMessage());
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getErrorMessage());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}

	// 서버, DB 예외
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("Exception : ", e);
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getErrorMessage());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}
}
