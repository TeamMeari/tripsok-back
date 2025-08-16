package com.tripsok_back.model.place.tour;

import java.util.LinkedHashSet;
import java.util.Set;

import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.support.BaseModifiableEntity;
import com.tripsok_back.util.TimeUtil;

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
@Table(name = "TOUR", schema = "TRIPSOK")
public class Tour extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "tour_seq", sequenceName = "TOUR_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tour_seq")
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Size(max = 255)
	@Column(name = "TOUR_TYPE")
	private String tourType;

	@OneToMany(mappedBy = "tour")
	private Set<Attraction> attractions = new LinkedHashSet<>();

	@OneToMany(mappedBy = "tour")
	private Set<Place> places = new LinkedHashSet<>();

	@OneToMany(mappedBy = "tour")
	private Set<TourImage> tourImages = new LinkedHashSet<>();

	@OneToMany(mappedBy = "tour")
	private Set<TourReview> tourReviews = new LinkedHashSet<>();

	public static Tour buildTour(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto) {
		Tour tour = new Tour();

		tour.setTourType(detailResponseDto.getLargeClassificationSystem1());
		tour.setCreatedAt(TimeUtil.stringToLocalDateTime(placeDto.getCreatedTime()));

		return tour;
	}
}