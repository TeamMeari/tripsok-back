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
public interface TourRepository extends JpaRepository<Place, Integer> {
	@Override
	@EntityGraph(attributePaths = {"placeTrs", "tour.tourImages"})
	Optional<Place> findById(Integer id);

	@EntityGraph(attributePaths = {"placeTrs", "tour"})
	Optional<Place> findByContentId(Integer contentId);

	@EntityGraph(attributePaths = {"tour"})
	Page<Place> findByTourIsNotNullAndPlaceTrs_Id_Locale(
		String locale, Pageable pageable
	);

	@EntityGraph(attributePaths = {"placeTrs", "tour", "themes", "themes.theme"})
	Page<Place> findByTourIsNotNullAndThemes_Theme_Id(Pageable pageable, Integer themeId);

	@EntityGraph(attributePaths = {"placeTrs", "tour.tourImages"})
	Page<Place> findAllByTourIsNotNull(PageRequest of);

	@EntityGraph(attributePaths = {"placeTrs", "tour.tourImages", "tour.placeLclsCategory.placeLclsCategoryTrs",
		"themes", "themes.theme"})
	Page<Place> findAllByTourIsNotNullOrderByIdAsc(PageRequest of);
}
