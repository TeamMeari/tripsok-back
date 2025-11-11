package com.tripsok_back.repository.user;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.user.InterestPlace;
import com.tripsok_back.model.user.TripSokUser;

@Repository
public interface InterestPlaceRepository extends JpaRepository<InterestPlace, Integer> {
	InterestPlace findInterestPlaceByUserAndPlace(TripSokUser user, Place place);

	@Query("SELECT ip FROM InterestPlace ip WHERE ip.user = :user AND (:lastId IS NULL OR ip.id < :lastId) AND (:type IS NULL OR (:type = \"ACCOMMODATION\" AND ip.place.accommodation IS NOT NULL) OR (:type = \"RESTAURANT\" AND ip.place.restaurant IS NOT NULL) OR (:type = \"TOUR\" AND ip.place.tour IS NOT NULL)) ORDER BY ip.id DESC")
	Slice<InterestPlace> findInterestPlacesByUser(TripSokUser user, Pageable pageable, Integer lastId, String type);

	@Query("SELECT count(ip) FROM InterestPlace ip WHERE ip.user = :user AND (:type = \"ACCOMMODATION\" AND ip.place.accommodation IS NOT NULL OR :type = \"RESTAURANT\" AND ip.place.restaurant IS NOT NULL OR :type = \"TOUR\" AND ip.place.tour IS NOT NULL)")
	Integer countByUserAndCategory(TripSokUser user, String type);

	boolean existsByPlaceAndUser_Id(Place place, Integer userId);

List<InterestPlace> findByUser_IdAndPlace_IdIn(Integer userId, Collection<Integer> placeIds);

	List<InterestPlace> findByUser_IdAndPlace_In(Integer userId, List<Place> places);
}
