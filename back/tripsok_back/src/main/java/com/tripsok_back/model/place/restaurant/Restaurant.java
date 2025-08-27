package com.tripsok_back.model.place.restaurant;

import com.tripsok_back.dto.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.TourApiPlaceResponseDto;
import com.tripsok_back.support.BaseModifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RESTAURANT")
public class Restaurant extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "restaurant_seq", sequenceName = "RESTAURANT_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_seq")
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Size(max = 255)
	@Column(name = "RESTAURANT_TYPE")
	private String restaurantType;

	public static Restaurant buildRestaurant(TourApiPlaceResponseDto placeDto,
		TourApiPlaceDetailResponseDto detailResponseDto) {
		Restaurant restaurant = new Restaurant();

		restaurant.setRestaurantType(
			detailResponseDto.getLargeClassificationSystem1());

		return restaurant;
	}

}
