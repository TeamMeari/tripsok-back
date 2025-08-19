package com.tripsok_back.repository.place.accomodation;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.accommodation.Accommodation;

@Repository
public interface AccommodationDetailRepository extends JpaRepository<Accommodation, Integer> {
	@EntityGraph(attributePaths = {"accommodation"})
	Optional<Accommodation> findById(Integer Id);
}
