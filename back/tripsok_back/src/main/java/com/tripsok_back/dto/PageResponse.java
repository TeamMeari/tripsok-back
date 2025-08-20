package com.tripsok_back.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
	private int currentPage;
	private int totalPages;
	private long totalItems;
	private List<T> items;

	public static <T> PageResponse<T> fromPage(Page<T> page) {
		return new PageResponse<>(
			page.getNumber(),
			page.getTotalPages(),
			page.getTotalElements(),
			page.getContent()
		);
	}
}
