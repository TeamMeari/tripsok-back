package com.tripsok_back.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.user.SocialType;
import com.tripsok_back.model.user.TripSokUser;

@Repository
public interface UserRepository extends JpaRepository<TripSokUser, Integer> {
	Optional<TripSokUser> findByEmailAndSocialType(String email, SocialType socialType);

	TripSokUser findBySocialIdAndSocialType(String socialId, SocialType socialType);

	TripSokUser findByEmail(String email);

	boolean existsByName(String name);
}
