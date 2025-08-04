package com.tripsok_back.model.place.restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RESTAURANT")
public class Restaurant {
	@Id
	@Column(name = "RESTAURANT_ID", nullable = false)
	private Long id;

	@Size(max = 255)
	@Column(name = "RESTAURANT_TYPE")
	private String restaurantType;

}