package com.tripsok_back.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	public static LocalDateTime stringToLocalDateTime(String stringTime) {
		return LocalDateTime.parse(stringTime, FORMATTER);
	}
}
