package com.tripsok_back.model.place.restaurant;

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
@Table(name = "RESTAURANT")
public class Restaurant extends BaseModifiableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESTAURANT_id_gen")
	@SequenceGenerator(name = "RESTAURANT_id_gen", sequenceName = "GLOBAL_PLACE_SEQ", allocationSize = 1)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "PLACE_LCLS_CATEGORY_ID", nullable = false)
	private PlaceLclsCategory placeLclsCategory;

	@OneToMany(mappedBy = "restaurant")
	private Set<Menu> menus = new LinkedHashSet<>();

	@OneToOne(mappedBy = "restaurant")
	private Place place;

	@OneToMany(mappedBy = "restaurant", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	private Set<RestaurantImage> restaurantImages = new LinkedHashSet<>();

	@OneToMany(mappedBy = "restaurant")
	private Set<RestaurantReview> restaurantReviews = new LinkedHashSet<>();

	public static Restaurant buildRestaurant(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
		Restaurant restaurant = new Restaurant();

		//restaurant.setRestaurantType(
		//	detailResponseDto.getLargeClassificationSystem1());

		return restaurant;
	}

	public void addImageUrl(String image) {
		RestaurantImage newImage = RestaurantImage.buildUrlImage(image);
		this.restaurantImages.add(newImage);
		newImage.setRestaurant(this);
	}

	public List<String> getImageUrlList() {
		List<String> imageUrlList = new ArrayList<>();
		for (RestaurantImage restaurantImage : restaurantImages) {
			//TODO: 향후 이미지 버킷 사용시 이미지 url 가져오는 기능 추가
			String url = (restaurantImage.getUrl().isEmpty()) ? "" : restaurantImage.getUrl();
			imageUrlList.add(url);
		}
		if (imageUrlList.isEmpty())
			imageUrlList.add("");
		return imageUrlList;
	}
}
