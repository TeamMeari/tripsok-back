package com.tripsok_back.model.place;

import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE_LCLS_CATEGORY", schema = "TRIPSOK")
public class PlaceLclsCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

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
		PlaceLclsCategory entity = new PlaceLclsCategory();
		entity.setLclsSystm1Code(dto.getLclsSystm1Cd());
		entity.setLclsSystm1Name(dto.getLclsSystm1Nm());
		entity.setLclsSystm2Code(dto.getLclsSystm2Cd());
		entity.setLclsSystm2Name(dto.getLclsSystm2Nm());
		entity.setLclsSystm3Code(dto.getLclsSystm3Cd());
		entity.setLclsSystm3Name(dto.getLclsSystm3Nm());
		return entity;
	}

	public void updateCategory(LclsCategoryItemResponseDto dto) {
		this.lclsSystm3Name = dto.getLclsSystm3Nm();
	}
}