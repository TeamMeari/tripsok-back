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
public class PlaceTrId implements java.io.Serializable {
	private static final long serialVersionUID = -4061772191487864406L;
	@NotNull
	@Column(name = "PLACE_ID", nullable = false)
	private Long placeId;

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
		PlaceTrId entity = (PlaceTrId)o;
		return Objects.equals(this.placeId, entity.placeId) &&
			Objects.equals(this.locale, entity.locale);
	}

	@Override
	public int hashCode() {
		return Objects.hash(placeId, locale);
	}

}