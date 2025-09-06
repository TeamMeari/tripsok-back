package com.tripsok_back.repository.email;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.auth.EmailVerificationToken;

@Repository
public interface RedisEmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, Integer> {
	EmailVerificationToken findByEmail(String email);
}
