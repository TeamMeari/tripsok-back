package com.tripsok_back.model.place;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RESTAURANT_IMAGE")
public class RestaurantImage {
	@Id
	@Column(name = "RESTAURANT_IMAGE_ID", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "RESTAURANT_ID", nullable = false)
	private Long restaurantId;

	@NotNull
	@Column(name = "MENU_ID", nullable = false)
	private Long menuId;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}