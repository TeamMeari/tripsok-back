package com.tripsok_back.model.place.restaurant;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MENU")
public class Menu {
	@Id
	@Column(name = "MENU_ID", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "RESTAURANT_ID", nullable = false)
	private Long restaurantId;

	@Size(max = 255)
	@Column(name = "MENU_NAME")
	private String menuName;

	@Size(max = 255)
	@Column(name = "MENU_PRICE")
	private String menuPrice;

	@Size(max = 255)
	@Column(name = "MENU_INFORMATION")
	private String menuInformation;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}