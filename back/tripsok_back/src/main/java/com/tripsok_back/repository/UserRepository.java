package com.tripsok_back.repository;

import com.tripsok_back.model.TripSokUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<TripSokUser, Long> {
}