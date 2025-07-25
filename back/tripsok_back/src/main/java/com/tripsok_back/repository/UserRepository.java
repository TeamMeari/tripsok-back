package com.tripsok_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.user.SocialType;
import com.tripsok_back.model.user.TripSokUser;

@Repository
public interface UserRepository extends JpaRepository<TripSokUser, Integer> {
	TripSokUser findByEmailAndSocialType(String email, SocialType socialType);
}
