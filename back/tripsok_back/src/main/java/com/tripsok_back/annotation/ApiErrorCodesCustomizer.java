package com.tripsok_back.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.exception.handler.ErrorResponse;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Component
public class ApiErrorCodesCustomizer implements OperationCustomizer {
	@Override
	public Operation customize(Operation operation, HandlerMethod handlerMethod) {
		ApiErrorCodes annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(),
			ApiErrorCodes.class);
		if (annotation != null) {
			// status code별로 ErrorCode 그룹화
			Map<Integer, List<ErrorCode>> grouped = new HashMap<>();
			for (ErrorCode errorCode : annotation.value()) {
				grouped.computeIfAbsent(errorCode.getHttpStatus().value(), k -> new ArrayList<>()).add(errorCode);
			}
			// 각 status code에 대해 예시 추가
			for (Map.Entry<Integer, List<ErrorCode>> entry : grouped.entrySet()) {
				ApiResponse apiResponse = new ApiResponse().description("사용자 정의 오류 응답");
				MediaType mediaType = new io.swagger.v3.oas.models.media.MediaType();
				for (ErrorCode errorCode : entry.getValue()) {
					Example example = new Example();
					example.setSummary(errorCode.name());
					example.setValue(new ErrorResponse(errorCode.getCode(), errorCode.getErrorMessage()));
					mediaType.addExamples(errorCode.name(), example);
				}
				apiResponse.setContent(
					new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType));
				operation.getResponses().addApiResponse(String.valueOf(entry.getKey()), apiResponse);
			}
		}
		return operation;
	}
}
