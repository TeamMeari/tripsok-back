package com.tripsok_back.model.place;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.accommodation.Accommodation;
import com.tripsok_back.model.place.restaurant.Restaurant;
import com.tripsok_back.model.place.tour.Tour;
import com.tripsok_back.support.BaseModifiableEntity;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.util.TimeUtil;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Table(name = "PLACE")
@AttributeOverrides({
	@AttributeOverride(name = "createdAt", column = @Column(name = "CREATED_AT", nullable = false)),
	@AttributeOverride(name = "updatedAt", column = @Column(name = "UPDATED_AT", nullable = false))
})
public class Place extends BaseModifiableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLACE_id_gen")
	@SequenceGenerator(name = "PLACE_id_gen", sequenceName = "GLOBAL_PLACE_SEQ", allocationSize = 1)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@NotNull
	@Column(name = "CONTENT_ID", nullable = false)
	private Integer contentId;

	@Size(max = 255)
	@Column(name = "CONTACT")
	private String contact;

	@Size(max = 255)
	@Column(name = "EMAIL")
	private String email;

	@NotNull
	@ColumnDefault("0")
	@Column(name = "\"view\"", nullable = false)
	private Integer view;

	@NotNull
	@ColumnDefault("0")
	@Column(name = "\"like\"", nullable = false)
	private Integer like;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "TOUR_ID")
	private Tour tour;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "RESTAURANT_ID")
	private Restaurant restaurant;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "ACCOMMODATION_ID")
	private Accommodation accommodation;

	@Column(name = "MAP_X", precision = 13, scale = 10)
	private BigDecimal mapX;

	@Column(name = "MAP_Y", precision = 13, scale = 10)
	private BigDecimal mapY;

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PlaceTr> placeTrs = new LinkedHashSet<>();

	public PlaceTr getPlaceTr(String language) {
		if (language == null)
			return null;
		String normalized = language.toLowerCase();
		for (PlaceTr placeTr : placeTrs) {
			if (placeTr.getId() != null && normalized.equals(placeTr.getId().getLocale())) {
				return placeTr;
			}
		}
		return null;
	}

	public PlaceTr getPlaceTr(LocaleCode localeCode) {
		if (localeCode == null)
			return null;
		for (PlaceTr placeTr : placeTrs) {
			if (placeTr.getId() != null && localeCode == placeTr.getId().getLocaleCode()) {
				return placeTr;
			}
		}
		return null;
	}

	public void initPlaceTrsWithKorean(String name, String address, String information, String summary) {
		List<LocaleCode> targets = List.of(LocaleCode.KO, LocaleCode.EN, LocaleCode.JA, LocaleCode.CN);
		for (LocaleCode lc : targets) {
			PlaceTr tr = getOrCreateTr(lc);
			if (lc == LocaleCode.KO) {
				tr.setPlaceName(name);
				tr.setAddress(address);
				tr.setInformation(information);
				tr.setSummary(summary);
			}
			//TODO: 업데이트 번역
		}
	}

	private PlaceTr getOrCreateTr(LocaleCode lc) {
		PlaceTr existing = getPlaceTr(lc);
		if (existing != null)
			return existing;
		PlaceTr tr = new PlaceTr();
		PlaceTrId id = new PlaceTrId();
		id.setLocaleCode(lc);
		// Ensure composite key consistency for existing Place
		if (this.id != null) {
			id.setPlaceId(this.id);
		}
		tr.setId(id);
		tr.setPlace(this);
		placeTrs.add(tr);
		return tr;
	}

	public void upsertKorean(String name, String address, String information, String summary) {
		initPlaceTrsWithKorean(name, address, information, summary);
	}

	public void upsertTranslation(LocaleCode lc, String name, String address, String information, String summary) {
		if (lc == null || lc == LocaleCode.KO)
			return;
		PlaceTr tr = getOrCreateTr(lc);
		tr.setPlaceName(name);
		tr.setAddress(address);
		tr.setInformation(information);
		tr.setSummary(summary);
	}

	public static Place buildAccommodation(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, PlaceLclsCategory categoryName, String summary) {
		Place place = new Place();

		place.setContentId(placeDto.getContentId());
		place.initPlaceTrsWithKorean(placeDto.getTitle(),
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""),
			detailResponseDto.getOverview(), summary);
		place.setContact(placeDto.getPhoneNumber());
		place.setEmail(null);
		place.setView(0);
		place.setLike(0);

		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			place.setMapX(new BigDecimal(placeDto.getLongitude()));
			place.setMapY(new BigDecimal(placeDto.getLatitude()));
		}

		place.setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));

		place.setTour(null);
		place.setRestaurant(null);
		place.setAccommodation(Accommodation.buildAccommodation(placeDto, detailResponseDto, categoryName));
		Accommodation accommodation = place.getAccommodation();
		accommodation.setPlaceLclsCategory(categoryName);
		accommodation.addImageUrl(detailResponseDto.getFirstImageUrl());
		accommodation.addImageUrl(detailResponseDto.getFirstImageUrlSecondary());
		return place;
	}

	public static Place buildAccommodation(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, PlaceLclsCategory categoryName) {
		return buildAccommodation(placeDto, detailResponseDto, categoryName, null);
	}

	public static Place buildTour(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, PlaceLclsCategory categoryName, String summary) {
		Place place = new Place();

		place.setContentId(placeDto.getContentId());
		place.initPlaceTrsWithKorean(placeDto.getTitle(),
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""),
			detailResponseDto.getOverview(), summary);
		place.setContact(placeDto.getPhoneNumber());
		place.setEmail(null);
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
		tour.setPlaceLclsCategory(categoryName);
		tour.addImageUrl(detailResponseDto.getFirstImageUrl());
		tour.addImageUrl(detailResponseDto.getFirstImageUrlSecondary());
		return place;
	}

	public static Place buildTour(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, PlaceLclsCategory categoryName) {
		return buildTour(placeDto, detailResponseDto, categoryName, null);
	}

	public static Place buildRestaurant(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, PlaceLclsCategory categoryName, String summary) {
		Place place = new Place();

		place.setContentId(placeDto.getContentId());
		place.initPlaceTrsWithKorean(placeDto.getTitle(),
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""),
			detailResponseDto.getOverview(), summary);
		place.setContact(placeDto.getPhoneNumber());
		place.setEmail(null);
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
		restaurant.setPlaceLclsCategory(categoryName);
		restaurant.addImageUrl(detailResponseDto.getFirstImageUrl());
		restaurant.addImageUrl(detailResponseDto.getFirstImageUrlSecondary());
		return place;
	}

	public static Place buildRestaurant(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto, PlaceLclsCategory categoryName) {
		return buildRestaurant(placeDto, detailResponseDto, categoryName, null);
	}

	public void updateAccommodation(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto,
		PlaceLclsCategory categoryName) {
		upsertKorean(
			placeDto.getTitle(),
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""),
			detailResponseDto.getOverview(),
			null
		);

		this.contact = placeDto.getPhoneNumber();
		this.accommodation.setPlaceLclsCategory(categoryName);
		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}

	public void updateRestaurant(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto,
		PlaceLclsCategory categoryName) {
		upsertKorean(
			placeDto.getTitle(),
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""),
			detailResponseDto.getOverview(),
			null
		);
		this.contact = placeDto.getPhoneNumber();
		this.restaurant.setPlaceLclsCategory(categoryName);
		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}

	public void updateTour(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto,
		PlaceLclsCategory categoryName) {
		upsertKorean(
			placeDto.getTitle(),
			placeDto.getAddress() + (placeDto.getAddressDetail() != null ? " " + placeDto.getAddressDetail() : ""),
			detailResponseDto.getOverview(),
			null
		);
		this.contact = placeDto.getPhoneNumber();
		this.tour.setPlaceLclsCategory(categoryName);
		if (placeDto.getLongitude() != null && placeDto.getLatitude() != null) {
			this.mapX = new BigDecimal(placeDto.getLongitude());
			this.mapY = new BigDecimal(placeDto.getLatitude());
		}

		setUpdatedAt(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
	}

	public void updateNullRestaurantDetail(TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto,
		PlaceLclsCategory category) {
		Restaurant restaurant = this.getRestaurant();
		restaurant.setPlaceLclsCategory(category);
		restaurant.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrl());
		restaurant.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrlSecondary());
	}

	public void updateNullAccommodationDetail(TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto,
		PlaceLclsCategory category) {
		Accommodation accommodation = this.getAccommodation();
		accommodation.setPlaceLclsCategory(category);
		accommodation.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrl());
		accommodation.addImageUrl(tourApiPlaceDetailResponseDto.getFirstImageUrlSecondary());
	}

	public void updateNullTourDetail(TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto,
		PlaceLclsCategory category) {
		Tour tour = this.getTour();
		tour.setPlaceLclsCategory(category);
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
