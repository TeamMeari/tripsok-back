package com.tripsok_back.model.place;

import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE_LCLS_CATEGORY")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class PlaceLclsCategory {
	@EmbeddedId
	private PlaceLclsCategoryId id;

	@Size(max = 10)
	@NotNull
	@Column(name = "LCLS_SYSTM1_CODE", nullable = false, length = 10)
	private String lclsSystm1Code;

	@Size(max = 100)
	@NotNull
	@Column(name = "LCLS_SYSTM1_NAME", nullable = false, length = 100)
	private String lclsSystm1Name;

	@Size(max = 20)
	@NotNull
	@Column(name = "LCLS_SYSTM2_CODE", nullable = false, length = 20)
	private String lclsSystm2Code;

	@Size(max = 100)
	@NotNull
	@Column(name = "LCLS_SYSTM2_NAME", nullable = false, length = 100)
	private String lclsSystm2Name;

	@Size(max = 20)
	@NotNull
	@Column(name = "LCLS_SYSTM3_CODE", nullable = false, length = 20)
	private String lclsSystm3Code;

	@Size(max = 200)
	@NotNull
	@Column(name = "LCLS_SYSTM3_NAME", nullable = false, length = 200)
	private String lclsSystm3Name;

	public static PlaceLclsCategory fromDto(LclsCategoryItemResponseDto dto) {
		return PlaceLclsCategory.builder()
			.lclsSystm1Code(dto.getLclsSystm1Cd())
			.lclsSystm1Name(dto.getLclsSystm1Nm())
			.lclsSystm2Code(dto.getLclsSystm2Cd())
			.lclsSystm2Name(dto.getLclsSystm2Nm())
			.lclsSystm3Code(dto.getLclsSystm3Cd())
			.lclsSystm3Name(dto.getLclsSystm3Nm())
			.build();
	}
}