package com.tripsok_back.repository.tripplan;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.tripplan.TripPlan;

@Repository
public interface TripPlanRepository extends JpaRepository<TripPlan, Integer> {
	Optional<TripPlan> findByUserId(Integer userId);
}
