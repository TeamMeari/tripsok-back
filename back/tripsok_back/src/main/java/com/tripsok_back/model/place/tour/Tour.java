package com.tripsok_back.model.place.tour;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceLclsCategory;
import com.tripsok_back.support.BaseModifiableEntity;
import com.tripsok_back.util.TimeUtil;

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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TOUR")
public class Tour extends BaseModifiableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TOUR_id_gen")
	@SequenceGenerator(name = "TOUR_id_gen", sequenceName = "GLOBAL_PLACE_SEQ", allocationSize = 1)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "PLACE_LCLS_CATEGORY_ID", nullable = false)
	private PlaceLclsCategory placeLclsCategory;

	@OneToMany(mappedBy = "tour")
	private Set<Attraction> attractions = new LinkedHashSet<>();

	@OneToOne(mappedBy = "tour")
	private Place place;

	@OneToMany(mappedBy = "tour", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 100)
	private Set<TourImage> tourImages = new LinkedHashSet<>();

	@OneToMany(mappedBy = "tour")
	@Fetch(FetchMode.SUBSELECT)
	private Set<TourReview> tourReviews = new LinkedHashSet<>();

	public static Tour buildTour(TourApiPlaceResponseDto placeDto, TourApiPlaceDetailResponseDto detailResponseDto) {
		Tour tour = new Tour();

		//tour.setTourType(detailResponseDto.getLargeClassificationSystem1());
		tour.setCreatedAt(TimeUtil.stringToLocalDateTime(placeDto.getCreatedTime()));

		return tour;
	}

	public void addImageUrl(String image) {
		TourImage newImage = TourImage.buildUrlImage(image);
		this.tourImages.add(newImage);
		newImage.setTour(this);
	}

	public List<String> getImageUrlList() {
		List<String> imageUrlList = new ArrayList<>();
		for (TourImage tourImage : tourImages) {
			//TODO: 향후 이미지 버킷 사용시 이미지 url 가져오는 기능 추가
			String url = (tourImage.getUrl().isEmpty()) ? "" : tourImage.getUrl();
			imageUrlList.add(url);
		}
		if (imageUrlList.isEmpty())
			imageUrlList.add("");
		return imageUrlList;
	}
}
