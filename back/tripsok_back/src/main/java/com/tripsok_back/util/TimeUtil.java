package com.tripsok_back.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

	public static Instant StringToInstant(String stringTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime localDateTime = LocalDateTime.parse(stringTime, formatter);
		Instant instant = localDateTime.toInstant(ZoneOffset.ofHours(9));
		return instant;
	}
}
