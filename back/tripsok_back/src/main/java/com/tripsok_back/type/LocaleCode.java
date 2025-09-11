package com.tripsok_back.type;

import lombok.Getter;

@Getter
public enum LocaleCode {
	KO("ko", "korean", "KorService2"),
	EN("en", "English", "EngService2"),
	JA("ja", "Japanese", "JpnService2"),
	CN("cn", "Chinese (Traditional)", "ChtService2");

	private final String code;
	private final String language;
	private final String urlPath;

	LocaleCode(String code, String language, String urlPath) {
		this.code = code;
		this.language = language;
		this.urlPath = urlPath;
	}

	public static LocaleCode from(String code) {
		if (code == null)
			return null;
		String normalized = code.toLowerCase();
		for (LocaleCode value : values()) {
			if (value.code.equals(normalized)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unsupported locale code: " + code);
	}
}
