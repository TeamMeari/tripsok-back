package com.tripsok_back.model.tripplan;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

import com.tripsok_back.dto.tripplan.request.UpdateTripPlanRequest;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.support.BaseModifiableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TRIP_PLAN", indexes = {
	@Index(name = "idx_trip_plan_user_id", columnList = "user_id")
}, uniqueConstraints = {
	@UniqueConstraint(name = "uk_trip_plan_user", columnNames = {"user_id"})
})
@NoArgsConstructor
public class TripPlan extends BaseModifiableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "TRIP_DATE")
	private LocalDate tripDate;

	@Column(name = "START_TIME")
	private LocalTime startTime;

	@Column(name = "NUMBER_OF_PEOPLE")
	private Integer numberOfPeople;

	@Column(name = "STATUS", length = 20, nullable = false)
	@Enumerated(EnumType.STRING) // DRAFT(임시저장) COMPLETED(결제완료)
	private PlanStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private TripSokUser user;

	@Version
	@Column(name = "VERSION", nullable = false)
	private Integer expectedVersion;        // 낙관적 락(자동 임시저장 충돌 방지)

	@OneToMany(mappedBy = "tripPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<VisitSpot> VisitSpotSet = Collections.emptySet();

	public enum PlanStatus { DRAFT, COMPLETED}

	public TripPlan(TripSokUser user){
		this.user = user;
		this.status = PlanStatus.DRAFT;
		this.expectedVersion = 0;
	}

	public void updateTripPlan(UpdateTripPlanRequest request, Set<VisitSpot> visitSpotSet) {
		this.tripDate = request.tripPlan().getTripDate();
		this.startTime = request.tripPlan().getStartTime();
		this.numberOfPeople = request.tripPlan().getNumberOfPeople();
		this.VisitSpotSet.clear();
		this.VisitSpotSet.addAll(visitSpotSet);
	}

}
