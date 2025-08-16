package com.tripsok_back.security.service;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tripsok_back.exception.AuthException;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.model.user.SocialType;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.UserRepository;
import com.tripsok_back.security.dto.TripSokUserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TripSokUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		TripSokUser user = userRepository.findByEmailAndSocialType(username, SocialType.EMAIL)
			.orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
		log.info("사용자 정보 조회 성공(Id): {}", user.getId());
		return new TripSokUserDto(
			user.getId().toString(), user.getPassword(), user.getSocialType(),
			user.getRole().getAuthority().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
		);
	}
}
