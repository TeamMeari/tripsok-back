package com.tripsok_back.model.place.accommodation;

import java.util.LinkedHashSet;
import java.util.Set;

import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.support.BaseModifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ACCOMMODATION", schema = "TRIPSOK")
public class Accommodation extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "accommodation_seq", sequenceName = "ACCOMMODATION_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accommodation_seq")
	private Integer id;

	@Size(max = 255)
	@Column(name = "ACCOMMODATION_TYPE")
	private String accommodationType;

	@OneToMany(mappedBy = "accommodation")
	private Set<AccommodationImage> accommodationImages = new LinkedHashSet<>();

	@OneToMany(mappedBy = "accommodation")
	private Set<AccommodationReview> accommodationReviews = new LinkedHashSet<>();

	@OneToMany(mappedBy = "accommodation")
	private Set<Place> places = new LinkedHashSet<>();

	@OneToMany(mappedBy = "accommodation")
	private Set<Room> rooms = new LinkedHashSet<>();

	public static Accommodation buildAccommodation(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
		Accommodation accommodation = new Accommodation();

		accommodation.setAccommodationType(detailResponseDto.getLargeClassificationSystem1());

		return accommodation;
	}

	public void updateAccommodation(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto) {
		this.accommodationType = detailResponseDto.getLargeClassificationSystem1();
	}
}