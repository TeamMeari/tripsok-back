package com.tripsok_back.repository.place;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;

@Repository
public interface TourRepository extends JpaRepository<Place, Integer> {
	@Override
	@EntityGraph(attributePaths = {"tour.tourImages"})
	Optional<Place> findById(Integer id);

	@EntityGraph(attributePaths = {"tour"})
	Optional<Place> findByContentId(Integer contentId);

	@EntityGraph(attributePaths = {"tour"})
	Page<Place> findByTourIsNotNull(Pageable pageable);

	@EntityGraph(attributePaths = {"tour"})
	Page<Place> findByTourIsNotNullAndThemes_Theme_Id(Pageable pageable, Integer themeId);
}
