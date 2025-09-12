package com.tripsok_back.model.user;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.support.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
	@Index(name = "idx_interest_place_user_id", columnList = "user_id"),
	@Index(name = "idx_interest_place_place_id", columnList = "place_id")
},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_interest_place_user_place", columnNames = {"user_id", "place_id"})
	})
public class InterestPlace extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "user_id", nullable = false)
	private TripSokUser user;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	public InterestPlace(TripSokUser user, Place place) {
		this.user = user;
		this.place = place;
	}
}
