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
	@EntityGraph(attributePaths = {"placeTrs", "tour", "tour.tourImages", "tour.placeLclsCategory"})
	Optional<Place> findById(Integer id);

	@EntityGraph(attributePaths = {"placeTrs", "tour"})
	Optional<Place> findByContentId(Integer contentId);

	@EntityGraph(attributePaths = {"placeTrs", "tour.tourImages"})
	Page<Place> findByTourIsNotNull(Pageable pageable);
}
