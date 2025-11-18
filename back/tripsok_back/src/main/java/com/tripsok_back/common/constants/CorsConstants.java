package com.tripsok_back.common.constants;

import java.util.List;

public final class CorsConstants {
	public static final List<String> ALLOWED_ORIGINS = List.of(
		"http://localhost:8080",
		"http://localhost:5173",
		"http://localhost:3000",
		"http://localhost:4173",
		"https://trip-sok.jayden-bin.cc",
		"https://www.tourang.site",
		"https://tripsok-front.vercel.app"
	);
	public static final List<String> ALLOWED_HEADERS = List.of(
		"Authorization", "Content-Type"
	);

	public static final String ALLOWED_HEADERS_CSV = String.join(",", ALLOWED_HEADERS);

	private CorsConstants() {
	}

	public static boolean isAllowedOrigin(String origin) {
		for (String allowedOrigin : ALLOWED_ORIGINS) {
			if (origin.equals(allowedOrigin))
				return true;
		}
		return false;
	}
}
