package com.tripsok_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tripsok_back.model.theme.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {
	List<Theme> findAllByIdIn(List<Integer> ids);
	List<Theme> findAllByOrderByIdAsc();
}
