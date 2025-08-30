package com.tripsok_back.model.place.restaurant;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.support.BaseModifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RESTAURANT_REVIEW")
public class RestaurantReview extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "restaurant_review_seq", sequenceName = "RESTAURANT_REVIEW_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_review_seq")
	@Column(name = "ID", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "RESTAURANT_ID", nullable = false)
	private Restaurant restaurant;

	@Size(max = 255)
	@NotNull
	@Column(name = "USER_ID", nullable = false)
	private String userId;

	@Size(max = 255)
	@Column(name = "RESTAURANT_REVIEW")
	private String restaurantReview;

}
