package com.tripsok_back.model.place;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.dto.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.accommodation.Accommodation;
import com.tripsok_back.model.place.restaurant.Restaurant;
import com.tripsok_back.model.place.tour.Tour;
import com.tripsok_back.util.TimeUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE", schema = "TRIPSOK")
public class Place {
	@Id
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

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "TOUR_ID", nullable = false)
	private Tour tour;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "RESTAURANT_ID", nullable = false)
	private Restaurant restaurant;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "ACCOMMODATION_ID", nullable = false)
	private Accommodation accommodation;

	@Column(name = "MAP_X", precision = 4, scale = 10)
	private BigDecimal mapX;

	@Column(name = "MAP_Y", precision = 4, scale = 10)
	private BigDecimal mapY;

	@NotNull
	@Column(name = "CREATED_AT", nullable = false)
	private Instant createdAt;

	@NotNull
	@Column(name = "UPDATED_AT", nullable = false)
	private Instant updatedAt;

	public static Place buildAccommodation(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
		Place place = new Place();

		place.setContentId(placeDto.getContentId());
		place.setPlaceName(placeDto.getTitle());
		place.setAddress(
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""));
		place.setContact(placeDto.getPhoneNumber());
		place.setEmail(null); // Dto에 이메일 정보 없음
		place.setInformation(detailResponseDto.getOverview());
		place.setView(0); // 조회수는 외부 데이터 없음
		place.setLike(0); // 좋아요도 외부 데이터 없음

		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			place.setMapX(new BigDecimal(placeDto.getLongitude()));
			place.setMapY(new BigDecimal(placeDto.getLatitude()));
		}

		place.setCreatedAt(TimeUtil.StringToInstant(placeDto.getCreatedTime()));
		place.setUpdatedAt(TimeUtil.StringToInstant(placeDto.getModifiedTime()));

		place.setTour(new Tour()); // 실제 연관관계 객체는 Service단에서 세팅해야 함
		place.setRestaurant(new Restaurant()); // 프록시 or null 허용
		place.setAccommodation(new Accommodation()); // 프록시 or null 허용

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

		this.updatedAt = TimeUtil.StringToInstant(placeDto.getModifiedTime());
	}
}