package com.tripsok_back.repository.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {

}
