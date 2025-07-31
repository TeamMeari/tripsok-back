package com.tripsok_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tripsok_back.model.Theme.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {
}
