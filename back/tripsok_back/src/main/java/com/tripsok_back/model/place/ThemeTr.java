package com.tripsok_back.model.place;

import com.tripsok_back.model.theme.Theme;
import com.tripsok_back.support.BaseTimeEntity;
import com.tripsok_back.type.LocaleCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "THEME_TR", indexes = {
	@Index(name = "idx_theme_tr_theme_and_locale", columnList = "THEME_ID, LOCALE")
},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_theme_tr_theme_and_locale", columnNames = {"THEME_ID", "LOCALE"})
	})
@NoArgsConstructor
public class ThemeTr extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "THEME_ID", nullable = false)
	private Theme theme;

	@NotNull
	@Column(name = "LOCALE", nullable = false, length = 20)
	private LocaleCode locale;

	@Size(max = 100)
	@Column(name = "NAME", nullable = false, updatable = false)
	private String name;

	public ThemeTr(Theme theme, LocaleCode locale, String name) {
		this.theme = theme;
		this.locale = locale;
		this.name = name;
	}
}
