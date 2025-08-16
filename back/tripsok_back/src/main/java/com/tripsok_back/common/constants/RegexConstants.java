package com.tripsok_back.common.constants;

public class RegexConstants {
	// 비밀번호 정규식
	public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,20}$";
	public static final String PASSWORD_MESSAGE = "비밀번호는 8~20자 이하, 최소 하나의 영문자와 숫자 포함, 특수문자(!@#$%^&*)만 가능합니다.";

	// 닉네임 정규식
	public static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9\\p{Script=Hiragana}\\p{Script=Katakana}\\p{Script=Han}]{2,15}$";
	public static final String NICKNAME_MESSAGE = "닉네임은 2-15자 이하 문자(한글, 영문, 일본어, 중국어)와 숫자만 가능합니다. 공백은 허용되지 않습니다.";

	// 상수 클래스는 인스턴스화 방지
	private RegexConstants() {
		throw new IllegalStateException("상수 클래스는 인스턴스화할 수 없습니다");
	}
}
