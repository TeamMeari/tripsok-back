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
public interface AccommodationRepository extends JpaRepository<Place, Integer> {

	@Override
	@EntityGraph(attributePaths = {"placeTrs", "accommodation.accommodationImages", "tags", "tags.tag"})
	Optional<Place> findById(Integer id);

	@EntityGraph(attributePaths = {"placeTrs", "accommodation"})
	Optional<Place> findByContentId(Integer contentId);

	@EntityGraph(attributePaths = {"placeTrs", "accommodation.accommodationImages"})
	Page<Place> findByAccommodationIsNotNull(Pageable pageable);

	@EntityGraph(attributePaths = {"placeTrs", "accommodation", "themes", "themes.theme"})
	Page<Place> findByAccommodationIsNotNullAndThemes_Theme_Id(Pageable pageable, Integer themeId);

	@EntityGraph(attributePaths = {"placeTrs", "accommodation.accommodationImages"})
	Page<Place> findAllByAccommodationIsNotNull(PageRequest of);

	@EntityGraph(attributePaths = {"placeTrs", "accommodation.accommodationImages",
		"accommodation.placeLclsCategory.placeLclsCategoryTrs", "themes", "themes.theme"})
	Page<Place> findAllByAccommodationIsNotNullOrderByIdAsc(PageRequest of);
}
