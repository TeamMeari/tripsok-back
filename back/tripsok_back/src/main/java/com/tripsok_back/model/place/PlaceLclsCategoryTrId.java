package com.tripsok_back.model.place;

import java.util.Objects;

import org.hibernate.Hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class PlaceLclsCategoryTrId implements java.io.Serializable {
	private static final long serialVersionUID = -741258900667059267L;
	@NotNull
	@Column(name = "CATEGORY_ID", nullable = false)
	private Long categoryId;

	@Size(max = 20)
	@NotNull
	@Column(name = "LOCALE", nullable = false, length = 20)
	private String locale;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		PlaceLclsCategoryTrId entity = (PlaceLclsCategoryTrId)o;
		return Objects.equals(this.locale, entity.locale) &&
			Objects.equals(this.categoryId, entity.categoryId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(locale, categoryId);
	}

}