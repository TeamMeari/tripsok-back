package com.tripsok_back.repository.place;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;

@Repository
public interface RestaurantRepository extends JpaRepository<Place, Integer> {

	@EntityGraph(attributePaths = {"restaurant"})
	Optional<Place> findByContentId(Integer contentId);

}
