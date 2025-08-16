package com.tripsok_back.model.place.restaurant;

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
@Table(name = "RESTAURANT", schema = "TRIPSOK")
public class Restaurant extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "restaurant_seq", sequenceName = "RESTAURANT_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_seq")
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Size(max = 255)
	@Column(name = "RESTAURANT_TYPE")
	private String restaurantType;

	@OneToMany(mappedBy = "restaurant")
	private Set<Menu> menus = new LinkedHashSet<>();

	@OneToMany(mappedBy = "restaurant")
	private Set<Place> places = new LinkedHashSet<>();

	@OneToMany(mappedBy = "restaurant")
	private Set<RestaurantImage> restaurantImages = new LinkedHashSet<>();

	@OneToMany(mappedBy = "restaurant")
	private Set<RestaurantReview> restaurantReviews = new LinkedHashSet<>();

	public static Restaurant buildRestaurant(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
		Restaurant restaurant = new Restaurant();

		restaurant.setRestaurantType(
			detailResponseDto.getLargeClassificationSystem1());

		return restaurant;
	}

}
