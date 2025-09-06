package com.tripsok_back.repository.place;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;

@Repository
public interface RestaurantRepository extends JpaRepository<Place, Integer> {

	@Override
	@EntityGraph(attributePaths = {"restaurant.restaurantImages"})
	Optional<Place> findById(Integer id);

	@EntityGraph(attributePaths = {"restaurant"})
	Optional<Place> findByContentId(Integer contentId);

	@EntityGraph(attributePaths = {"restaurant"})
	Page<Place> findByRestaurantIsNotNull(Pageable pageable);

	@EntityGraph(attributePaths = {"restaurant"})
	Page<Place> findByRestaurantIsNotNullAndThemes_Theme_Id(Pageable pageable, Integer themeId);
}
