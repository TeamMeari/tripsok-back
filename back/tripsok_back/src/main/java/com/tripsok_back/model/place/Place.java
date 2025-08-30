package com.tripsok_back.model.place;

import java.math.BigDecimal;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.accommodation.Accommodation;
import com.tripsok_back.model.place.restaurant.Restaurant;
import com.tripsok_back.model.place.tour.Tour;
import com.tripsok_back.support.BaseModifiableEntity;
import com.tripsok_back.util.TimeUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE", schema = "TRIPSOK")
public class Place extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "global_place_seq", sequenceName = "GLOBAL_PLACE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_place_seq")
	@Column(name = "ID", nullable = false)
	private Integer id;

	@NotNull
	@Column(name = "CONTENT_ID", nullable = false)
	private Integer contentId;

	@Size(max = 255)
	@Column(name = "PLACE_NAME")
	private String placeName;

	@Size(max = 255)
	@Column(name = "ADDRESS")
	private String address;

	@Size(max = 255)
	@Column(name = "CONTACT")
	private String contact;

	@Size(max = 255)
	@Column(name = "EMAIL")
	private String email;

	@Lob
	@Column(name = "INFORMATION")
	private String information;

	@Column(name = "\"view\"")
	private Integer view;

	@Column(name = "\"like\"")
	private Integer like;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "TOUR_ID")
	private Tour tour;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "RESTAURANT_ID")
	private Restaurant restaurant;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "ACCOMMODATION_ID")
	private Accommodation accommodation;

	@Column(name = "MAP_X", precision = 13, scale = 10)
	private BigDecimal mapX;

	@Column(name = "MAP_Y", precision = 13, scale = 10)
	private BigDecimal mapY;

	public static Place buildAccommodation(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, String categoryName) {
		Place place = new Place();

		place.setContentId(placeDto.getContentId());
		place.setPlaceName(placeDto.getTitle());
		place.setAddress(
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""));
		place.setContact(placeDto.getPhoneNumber());
		place.setEmail(null);
		place.setInformation(detailResponseDto.getOverview());
		place.setView(0);
		place.setLike(0);

		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			place.setMapX(new BigDecimal(placeDto.getLongitude()));
			place.setMapY(new BigDecimal(placeDto.getLatitude()));
		}

		place.setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));

		place.setTour(null);
		place.setRestaurant(null);
		place.setAccommodation(Accommodation.buildAccommodation(placeDto, detailResponseDto));
		Accommodation accommodation = place.getAccommodation();
		accommodation.setAccommodationType(categoryName);
		accommodation.addImageUrl(detailResponseDto.getFirstImageUrl());
		accommodation.addImageUrl(detailResponseDto.getFirstImageUrlSecondary());
		return place;
	}

	public static Place buildTour(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, String categoryName) {
		Place place = new Place();

		place.setContentId(placeDto.getContentId());
		place.setPlaceName(placeDto.getTitle());
		place.setAddress(
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""));
		place.setContact(placeDto.getPhoneNumber());
		place.setEmail(null);
		place.setInformation(detailResponseDto.getOverview());
		place.setView(0);
		place.setLike(0);

		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			place.setMapX(new BigDecimal(placeDto.getLongitude()));
			place.setMapY(new BigDecimal(placeDto.getLatitude()));
		}

		place.setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));

		place.setAccommodation(null);
		place.setRestaurant(null);
		place.setTour(Tour.buildTour(placeDto, detailResponseDto));
		Tour tour = place.getTour();
		tour.setTourType(categoryName);
		tour.addImageUrl(detailResponseDto.getFirstImageUrl());
		tour.addImageUrl(detailResponseDto.getFirstImageUrlSecondary());
		return place;
	}

	public static Place buildRestaurant(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, String categoryName) {
		Place place = new Place();

		place.setContentId(placeDto.getContentId());
		place.setPlaceName(placeDto.getTitle());
		place.setAddress(
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""));
		place.setContact(placeDto.getPhoneNumber());
		place.setEmail(null);
		place.setInformation(detailResponseDto.getOverview());
		place.setView(0);
		place.setLike(0);

		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			place.setMapX(new BigDecimal(placeDto.getLongitude()));
			place.setMapY(new BigDecimal(placeDto.getLatitude()));
		}

		place.setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));

		place.setTour(null);
		place.setAccommodation(null);
		place.setRestaurant(Restaurant.buildRestaurant(placeDto, detailResponseDto));
		Restaurant restaurant = place.getRestaurant();
		restaurant.setRestaurantType(categoryName);
		restaurant.addImageUrl(detailResponseDto.getFirstImageUrl());
		restaurant.addImageUrl(detailResponseDto.getFirstImageUrlSecondary());
		return place;
	}

	public void updateAccommodation(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto,
		String categoryName) {
		this.placeName = placeDto.getTitle();
		this.address =
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : "");
		this.contact = placeDto.getPhoneNumber();
		this.information = detailResponseDto.getOverview();
		this.accommodation.setAccommodationType(categoryName);
		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}

	public void updateRestaurant(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto,
		String categoryName) {
		this.placeName = placeDto.getTitle();
		this.address =
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : "");
		this.contact = placeDto.getPhoneNumber();
		this.information = detailResponseDto.getOverview();
		this.restaurant.setRestaurantType(categoryName);
		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}

	public void updateTour(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto,
		String categoryName) {
		this.placeName = placeDto.getTitle();
		this.address =
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : "");
		this.contact = placeDto.getPhoneNumber();
		this.information = detailResponseDto.getOverview();
		this.tour.setTourType(categoryName);
		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}

	public void updateNullRestaurantDetail(TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto,
		String categoryName) {
		Restaurant restaurant = this.getRestaurant();
		restaurant.setRestaurantType(categoryName);
		restaurant.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrl());
		restaurant.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrlSecondary());
	}

	public void updateNullAccommodationDetail(TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto,
		String categoryName) {
		Accommodation accommodation = this.getAccommodation();
		accommodation.setAccommodationType(categoryName);
		accommodation.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrl());
		accommodation.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrlSecondary());
	}

	public void updateNullTourDetail(TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto,
		String categoryName) {
		Tour tour = this.getTour();
		tour.setTourType(categoryName);
		tour.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrl());
		tour.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrlSecondary());
	}

	public void incrementViewCount() {
		this.view++;
	}

	public void incrementLikeCount() {
		this.like++;
	}

}