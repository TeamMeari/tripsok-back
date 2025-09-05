package com.tripsok_back.model.place;

import com.tripsok_back.model.theme.Theme;
import com.tripsok_back.support.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE_THEME", indexes = {
	@Index(name = "idx_place_theme_theme", columnList = "THEME_ID"),
	@Index(name = "idx_place_theme_place", columnList = "PLACE_ID"),
},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_place_theme_place_and_theme", columnNames = {"PLACE_ID", "THEME_ID"})
	})
public class PlaceTheme extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "PLACE_ID", nullable = false)
	private Place place;

	@ManyToMany
	@JoinColumn(name = "THEME_ID", nullable = false)
	private Theme theme;
}
