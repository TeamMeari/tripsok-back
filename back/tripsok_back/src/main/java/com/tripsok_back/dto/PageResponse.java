package com.tripsok_back.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponse<E> {
	private int currentPage;
	private int totalPages;
	private long totalItems;
	private List<E> items;

	public static <T, E> PageResponse<E> fromPage(Page<T> page, Page<E> dtoPage) {
		return new PageResponse<>(
			page.getNumber(),
			page.getTotalPages(),
			page.getTotalElements(),
			dtoPage.getContent()
		);
	}
}
