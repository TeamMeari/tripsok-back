package com.tripsok_back.util;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.Transliterator;
import com.tripsok_back.dto.google.RomanizationResult;
import com.tripsok_back.type.LocaleCode;

public final class TransliterationUtil {
	private static final Transliterator ANY_LATIN = Transliterator.getInstance("Any-Latin; Latin-ASCII");
	private static final Transliterator ANY_KATAKANA = Transliterator.getInstance("Any-Katakana");

	private TransliterationUtil() {
	}

	public static boolean containsHangul(String text) {
		if (text == null)
			return false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if ((c >= '\uAC00' && c <= '\uD7A3') ||
				(c >= '\u1100' && c <= '\u11FF') |
					(c >= '\u3130' && c <= '\u318F')) {
				return true;
			}
		}
		return false;
	}

	public static List<RomanizationResult> romanize(String text, LocaleCode target) {
		List<RomanizationResult> list = new ArrayList<>();
		if (target == LocaleCode.JA) {
			String v = styleKatakana(ANY_KATAKANA.transliterate(text));
			list.add(new RomanizationResult(v, containsHangul(text) ? "ko" : null));
			return list;
		}

		String latin = ANY_LATIN.transliterate(text);
		if (target == LocaleCode.CN) {
			latin = styleLatinForCN(latin);
		} else {
			latin = styleLatinForEN(latin);
		}
		list.add(new RomanizationResult(latin, containsHangul(text) ? "ko" : null));
		return list;
	}

	public static String styleLatinForEN(String text) {
		if (text == null || text.isEmpty())
			return text;
		String[] tokens = text.trim().replaceAll("\\s+", " ").split(" ");
		StringBuilder styled = new StringBuilder(text.length());
		for (int i = 0; i < tokens.length; i++) {
			if (i > 0)
				styled.append(' ');
			styled.append(titleCaseToken(tokens[i]));
		}
		return styled.toString();
	}

	public static String styleLatinForCN(String text) {
		if (text == null || text.isEmpty())
			return text;
		return text.trim().replaceAll("\\s+", " ").toLowerCase();
	}

	public static String styleKatakana(String text) {
		if (text == null || text.isEmpty())
			return text;
		return text.trim().replaceAll("\\s+", " ");
	}

	private static String titleCaseToken(String token) {
		if (token.isEmpty())
			return token;
		char first = token.charAt(0);
		if (Character.isLetter(first)) {
			StringBuilder builder = new StringBuilder(token.length());
			builder.append(Character.toUpperCase(first));
			for (int i = 1; i < token.length(); i++) {
				char ch = token.charAt(i);
				builder.append(Character.toLowerCase(ch));
			}
			return builder.toString();
		}
		return token;
	}
}
