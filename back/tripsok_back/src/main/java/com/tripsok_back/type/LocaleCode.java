package com.tripsok_back.type;

import lombok.Getter;

@Getter
public enum LocaleCode {
	KO("ko", "korean", "KorService2", "Kore", "ko"),
	EN("en", "English", "EngService2", "Latn", "en"),
	JA("ja", "Japanese", "JpnService2", "Jpan", "ja"),
	CN("cn", "Chinese (Traditional)", "ChtService2", "Hant", "zh-TW");

	private final String code;
	private final String language;
	private final String urlPath;
	private final String script;
	private final String gcpLang;

	LocaleCode(String code, String language, String urlPath, String script, String gcpLang) {
		this.code = code;
		this.language = language;
		this.urlPath = urlPath;
		this.script = script;
		this.gcpLang = gcpLang;
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

	public String toGcpLang() {
		return gcpLang;
	}

	public String esNameAddrSuffix() {
		switch (this) {
			case KO:
				return "ko";
			case EN:
				return "en";
			case JA:
				return "ja";
			case CN:
				return "zh";
			default:
				return null;
		}
	}

	public String esSummaryInfoSuffix() {
		switch (this) {
			case KO:
				return "ko";
			case EN:
				return "en";
			case JA:
				return "ja";
			case CN:
				return "cn";
			default:
				return null;
		}
	}
}
