package com.tripsok_back.repository.place.accomodation;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;

@Repository
public interface AccommodationRepository extends JpaRepository<Place, Integer> {

	@Override
	@EntityGraph(attributePaths = {"accommodation.accommodationImages"})
	Optional<Place> findById(Integer id);

	@EntityGraph(attributePaths = {"accommodation"})
	Optional<Place> findByContentId(Integer contentId);

	@EntityGraph(attributePaths = {"accommodation"})
	Page<Place> findByAccommodationIsNotNull(Pageable pageable);
}
