package com.tripsok_back.repository.place;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.PlaceLclsCategory;

@Repository
public interface LclsCategoryRepository extends JpaRepository<PlaceLclsCategory, Integer> {
	Optional<PlaceLclsCategory> findByLclsSystm3Code(String code);
}
