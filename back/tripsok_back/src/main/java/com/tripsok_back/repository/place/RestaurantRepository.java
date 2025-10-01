package com.tripsok_back.repository.place;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;

@Repository
public interface RestaurantRepository extends JpaRepository<Place, Integer> {

	@Override
	@EntityGraph(attributePaths = {"placeTrs", "restaurant.restaurantImages", "tags", "tags.tag"})
	Optional<Place> findById(Integer id);

	@EntityGraph(attributePaths = {"placeTrs", "restaurant"})
	Optional<Place> findByContentId(Integer contentId);

	@EntityGraph(attributePaths = {"restaurant"})
	Page<Place> findByRestaurantIsNotNullAndPlaceTrs_Id_Locale(
		String locale, Pageable pageable
	);

	@EntityGraph(attributePaths = {"placeTrs", "restaurant", "themes", "themes.theme"})
	Page<Place> findByRestaurantIsNotNullAndThemes_Theme_Id(Pageable pageable, Integer themeId);

	@EntityGraph(attributePaths = {"placeTrs", "restaurant.restaurantImages"})
	Page<Place> findAllByRestaurantIsNotNull(PageRequest of);

	@EntityGraph(attributePaths = {"placeTrs", "restaurant.restaurantImages",
		"restaurant.placeLclsCategory.placeLclsCategoryTrs", "themes", "themes.theme"})
	Page<Place> findAllByRestaurantIsNotNullOrderByIdAsc(PageRequest of);
}
