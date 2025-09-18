package com.tripsok_back.repository.theme;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.theme.Theme;
import com.tripsok_back.model.theme.ThemeTr;
import com.tripsok_back.type.LocaleCode;

@Repository
public interface ThemeTrRepository extends JpaRepository<ThemeTr, Integer> {
    List<ThemeTr> findAllByLocaleOrderByThemeIdAsc(LocaleCode locale);

	List<ThemeTr> findAllByThemeInAndLocaleOrderByThemeIdAsc(List<Theme> theme, LocaleCode locale);
}
