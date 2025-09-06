package com.tripsok_back.model.place.accommodation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceLclsCategory;
import com.tripsok_back.support.BaseModifiableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "ACCOMMODATION", schema = "TRIPSOK")
public class Accommodation extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "global_place_seq", sequenceName = "GLOBAL_PLACE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_place_seq")
	private Integer id;

	@Size(max = 255)
	@Column(name = "ACCOMMODATION_TYPE")
	private String accommodationType;

	@OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<AccommodationImage> accommodationImages = new LinkedHashSet<>();

	@OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<AccommodationReview> accommodationReviews = new LinkedHashSet<>();

	@OneToOne(mappedBy = "accommodation")
	private Place place;

	@OneToMany(mappedBy = "accommodation")
	private Set<Room> rooms = new LinkedHashSet<>();

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "PLACE_LCLS_CATEGORY_ID", nullable = false)
	private PlaceLclsCategory placeLclsCategory;

	public static Accommodation buildAccommodation(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
		Accommodation accommodation = new Accommodation();

		accommodation.setAccommodationType(detailResponseDto.getLargeClassificationSystem1());

		return accommodation;
	}

	public void addImageUrl(String image) {
		AccommodationImage newImage = AccommodationImage.buildUrlImage(image);
		this.accommodationImages.add(newImage);
		newImage.setAccommodation(this);
	}

	public void addImageBucket(String bucket) {
		this.accommodationImages.add(new AccommodationImage());
	}

	public List<String> getImageUrlList() {
		List<String> imageUrlList = new ArrayList<>();
		for (AccommodationImage accommodationImage : accommodationImages) {
			//TODO: 향후 이미지 버킷 사용시 이미지 url 가져오는 기능 추가
			String url = (accommodationImage.getUrl().isEmpty()) ? "" : accommodationImage.getUrl();
			imageUrlList.add(url);
		}
		if (imageUrlList.isEmpty())
			imageUrlList.add("");
		return imageUrlList;
	}
}