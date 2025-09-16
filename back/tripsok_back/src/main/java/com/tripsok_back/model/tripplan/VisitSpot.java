package com.tripsok_back.model.tripplan;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.model.place.Place;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "VISIT_SPOT", indexes = {
	@Index(name = "idx_visit_spot_trip_plan_id", columnList = "TRIP_PLAN_ID"),
})
@NoArgsConstructor
public class VisitSpot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "TRIP_PLAN_ID", nullable = false)
	private TripPlan tripPlan;

	@Column(name = "ORDER_INDEX", nullable = false)
	Integer orderIndex;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "PLACE_ID", nullable = false)
	private Place place;

	@Column(name = "MEMO", length = 160)
	private String memo;

	public VisitSpot (Place place, String memo, Integer orderIndex, TripPlan tripPlan) {
		this.tripPlan = tripPlan;
		this.place = place;
		this.memo = memo;
		this.orderIndex = orderIndex;
	}
}
