package com.tripsok_back.repository.place;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
	@EntityGraph(attributePaths = {"placeTrs", "themes", "tags"})
	Page<Place> findByOrderByCreatedAtDesc(Pageable pageable);

	Integer countByAccommodationIsNotNull();

	Integer countByRestaurantIsNotNull();

	Integer countByTourIsNotNull();

	Set<Place> findByIdIn(Set<Integer> ids);
}
