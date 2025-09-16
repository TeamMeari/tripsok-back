package com.tripsok_back.model.place;

import com.tripsok_back.type.LocaleCode;

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
@Table(name = "PLACE_LCLS_CATEGORY_TR")
public class PlaceLclsCategoryTr {
	@EmbeddedId
	private PlaceLclsCategoryTrId id;

	@MapsId("categoryId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private PlaceLclsCategory placeLclsCategory;

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

	public static PlaceLclsCategoryTr of(PlaceLclsCategory parent,
		String locale,
		String l1, String l2, String l3) {
		PlaceLclsCategoryTr e = new PlaceLclsCategoryTr();
		e.setPlaceLclsCategory(parent);
		e.setId(new PlaceLclsCategoryTrId(parent.getId(), locale.toLowerCase()));
		e.setLclsSystm1Name(l1);
		e.setLclsSystm2Name(l2);
		e.setLclsSystm3Name(l3);
		return e;
	}

	public static PlaceLclsCategoryTr ofCascade(PlaceLclsCategory parent,
		LocaleCode locale, String l1, String l2, String l3) {
		PlaceLclsCategoryTr e = new PlaceLclsCategoryTr();
		PlaceLclsCategoryTrId id = new PlaceLclsCategoryTrId();
		id.setLocaleCode(locale);
		e.setId(id);
		e.setPlaceLclsCategory(parent);
		e.setLclsSystm1Name(l1);
		e.setLclsSystm2Name(l2);
		e.setLclsSystm3Name(l3);
		return e;
	}

	public static PlaceLclsCategoryTr ofKo(PlaceLclsCategory parent) {
		return of(parent, "ko",
			parent.getLclsSystm1Name(),
			parent.getLclsSystm2Name(),
			parent.getLclsSystm3Name());
	}
}
