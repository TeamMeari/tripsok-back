package com.tripsok_back.util;

import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonMapperUtil {

	private JsonMapperUtil() {
	}

	public static String json(ObjectMapper objectMapper, Object target) {
		if (target == null)
			return "null";
		try {
			return objectMapper.writeValueAsString(target);
		} catch (JsonProcessingException e) {
			return "JSON 직렬화 실패: " + e.getOriginalMessage();
		}
	}

	public static String pretty(ObjectMapper objectMapper, Object target) {
		if (target == null)
			return "null";
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(target);
		} catch (JsonProcessingException e) {
			return "JSON 직렬화 실패 " + e.getOriginalMessage();
		}
	}

	public static Supplier<String> prettyLazy(ObjectMapper objectMapper, Object target) {
		return () -> pretty(objectMapper, target);
	}
}
