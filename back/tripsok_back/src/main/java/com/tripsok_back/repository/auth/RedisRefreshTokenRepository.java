package com.tripsok_back.repository.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.auth.RefreshToken;

@Repository
public interface RedisRefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
	RefreshToken findByUserId(Integer userId);
}
