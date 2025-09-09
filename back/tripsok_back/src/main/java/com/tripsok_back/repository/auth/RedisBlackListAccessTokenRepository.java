package com.tripsok_back.repository.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.auth.BlackListAccessToken;

@Repository
public interface RedisBlackListAccessTokenRepository extends CrudRepository<BlackListAccessToken, Integer> {
	boolean existsByToken(String token);
}
