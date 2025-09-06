package com.tripsok_back.model.place;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE_LCLS_CATEGORY_TR", schema = "TRIPSOK")
public class PlaceLclsCategoryTr {
	@EmbeddedId
	private PlaceLclsCategoryTrId id;

	@MapsId("categoryId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private PlaceLclsCategory category;

	@Size(max = 100)
	@NotNull
	@Column(name = "LCLS_SYSTM1_NAME", nullable = false, length = 100)
	private String lclsSystm1Name;

	@Size(max = 100)
	@NotNull
	@Column(name = "LCLS_SYSTM2_NAME", nullable = false, length = 100)
	private String lclsSystm2Name;

	@Size(max = 200)
	@NotNull
	@Column(name = "LCLS_SYSTM3_NAME", nullable = false, length = 200)
	private String lclsSystm3Name;

}