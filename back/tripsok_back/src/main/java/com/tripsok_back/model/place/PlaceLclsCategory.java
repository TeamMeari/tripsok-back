package com.tripsok_back.model.place;

import java.util.HashSet;
import java.util.Set;

import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;
import com.tripsok_back.type.LocaleCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE_LCLS_CATEGORY")
public class PlaceLclsCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Size(max = 10)
	@NotNull
	@Column(name = "LCLS_SYSTM1_CODE", nullable = false, length = 10)
	private String lclsSystm1Code;

	@Size(max = 20)
	@NotNull
	@Column(name = "LCLS_SYSTM2_CODE", nullable = false, length = 20)
	private String lclsSystm2Code;

	@Size(max = 20)
	@NotNull
	@Column(name = "LCLS_SYSTM3_CODE", nullable = false, length = 20)
	private String lclsSystm3Code;

	@OneToMany(mappedBy = "placeLclsCategory", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PlaceLclsCategoryTr> placeLclsCategoryTrs = new HashSet<>();

	public static PlaceLclsCategory fromDto(LclsCategoryItemResponseDto dto) {
		PlaceLclsCategory entity = new PlaceLclsCategory();
		entity.setLclsSystm1Code(dto.getLclsSystm1Cd());
		entity.setLclsSystm2Code(dto.getLclsSystm2Cd());
		entity.setLclsSystm3Code(dto.getLclsSystm3Cd());
		return entity;
	}

	public String getLclsSystm1Name() {
		return getLclsSystm1Name(LocaleCode.KO);
	}

	public String getLclsSystm2Name() {
		return getLclsSystm2Name(LocaleCode.KO);
	}

	public String getLclsSystm3Name() {
		return getLclsSystm3Name(LocaleCode.KO);
	}

	public String getLclsSystm1Name(LocaleCode locale) {
		PlaceLclsCategoryTr tr = getTrByLocale(locale);
		return tr != null ? tr.getLclsSystm1Name() : null;
	}

	public String getLclsSystm2Name(LocaleCode locale) {
		PlaceLclsCategoryTr tr = getTrByLocale(locale);
		return tr != null ? tr.getLclsSystm2Name() : null;
	}

	public String getLclsSystm3Name(LocaleCode locale) {
		PlaceLclsCategoryTr tr = getTrByLocale(locale);
		return tr != null ? tr.getLclsSystm3Name() : null;
	}

	private PlaceLclsCategoryTr getTrByLocale(LocaleCode locale) {
		if (placeLclsCategoryTrs == null || placeLclsCategoryTrs.isEmpty())
			return null;
		for (PlaceLclsCategoryTr tr : placeLclsCategoryTrs) {
			if (tr.getId() != null && locale == tr.getId().getLocaleCode()) {
				return tr;
			}
		}
		for (PlaceLclsCategoryTr tr : placeLclsCategoryTrs) {
			if (tr.getId() != null && LocaleCode.KO == tr.getId().getLocaleCode()) {
				return tr;
			}
		}
		return placeLclsCategoryTrs.iterator().next();
	}

}
