package com.tripsok_back.repository.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.user.InterestPlace;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.type.PlaceJoinType;

@Repository
public interface InterestPlaceRepository extends JpaRepository<InterestPlace, Integer> {
	InterestPlace findInterestPlaceByUserAndPlace(TripSokUser user, Place place);

	@Query("SELECT ip FROM InterestPlace ip WHERE ip.user = :user AND (:lastId IS NULL OR ip.id < :lastId) AND (:type = com.tripsok_back.type.PlaceJoinType.ACCOMMODATION AND ip.place.accommodation IS NOT NULL) OR (:type = com.tripsok_back.type.PlaceJoinType.RESTAURANT AND ip.place.restaurant IS NOT NULL) OR (:type = com.tripsok_back.type.PlaceJoinType.TOUR AND ip.place.tour IS NOT NULL) ORDER BY ip.id desc")
	Slice<InterestPlace> findInterestPlacesByUser(TripSokUser user, Pageable pageable, Integer lastId,
		PlaceJoinType type);
}
