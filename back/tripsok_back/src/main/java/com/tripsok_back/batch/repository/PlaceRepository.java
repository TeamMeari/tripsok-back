package com.tripsok_back.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {

}
