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
import jakarta.persistence.ManyToOne;
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
	@SequenceGenerator(name = "place_seq", sequenceName = "PLACE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "place_seq")
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

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "TOUR_ID")
	private Tour tour;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "RESTAURANT_ID")
	private Restaurant restaurant;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "ACCOMMODATION_ID")
	private Accommodation accommodation;

	@Column(name = "MAP_X", precision = 13, scale = 10)
	private BigDecimal mapX;

	@Column(name = "MAP_Y", precision = 13, scale = 10)
	private BigDecimal mapY;

	public static Place buildAccommodation(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
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
		return place;
	}

	public static Place buildRestaurant(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
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
		place.setRestaurant(Restaurant.buildRestaurant(placeDto, detailResponseDto)); // Restaurant 엔티티에서 build 메서드 필요
		place.setAccommodation(null);

		return place;
	}

	public static Place buildTour(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto) {
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

		place.setTour(Tour.buildTour(placeDto, detailResponseDto)); // Tour 엔티티에서 build 메서드 필요
		place.setRestaurant(null);
		place.setAccommodation(null);

		return place;
	}

	public void updateAccommodation(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto) {
		this.placeName = placeDto.getTitle();
		this.address =
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : "");
		this.contact = placeDto.getPhoneNumber();
		this.information = detailResponseDto.getOverview();

		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}

	public void updateRestaurant(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto) {
		this.placeName = placeDto.getTitle();
		this.address =
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : "");
		this.contact = placeDto.getPhoneNumber();
		this.information = detailResponseDto.getOverview();

		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}

	public void updateTour(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto) {
		this.placeName = placeDto.getTitle();
		this.address =
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : "");
		this.contact = placeDto.getPhoneNumber();
		this.information = detailResponseDto.getOverview();

		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}
}