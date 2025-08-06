package com.tripsok_back.model.user;

import com.tripsok_back.support.BaseModifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripSokUser extends BaseModifiableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	private String socialId;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	private String password;

	private String countryCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(length = 100, nullable = false)
	private String contactEmail;

	@Builder
	public TripSokUser(String name, SocialType socialType, String socialId, String email, String password, Role role, String countryCode, String contactEmail) {
		this.name = name;
		this.socialType = socialType;
		this.socialId = socialId;
		this.email = email;
		this.password = password;
		this.role = role;
		this.countryCode = countryCode;
		this.contactEmail = contactEmail;
	}

	public static TripSokUser signUpUser(String nickname, SocialType socialType, String socialId, String email, String password, String countryCode) {
		return TripSokUser.builder()
			.name(nickname)
			.socialType(socialType)
			.socialId(socialId)
			.email(email)
			.password(password)
			.role(Role.USER)
			.countryCode(countryCode)
			.contactEmail(email)
			.build();
	}

	public void changePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public void changeCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void changeContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
}
