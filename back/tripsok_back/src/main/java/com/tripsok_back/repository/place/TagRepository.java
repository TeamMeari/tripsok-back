package com.tripsok_back.repository.place;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
	Optional<Tag> findByName(String name);
}
