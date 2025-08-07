package com.tripsok_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tripsok_back.model.user.InterestTheme;

public interface InterestThemeRepository extends JpaRepository<InterestTheme, Integer> {
}
