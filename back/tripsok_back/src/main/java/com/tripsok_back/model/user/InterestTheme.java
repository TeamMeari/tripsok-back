package com.tripsok_back.model.user;

import com.tripsok_back.support.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
	@Index(name = "idx_interest_theme_user_id", columnList = "user_id"),
	@Index(name = "idx_interest_theme_theme_id", columnList = "theme_id")
})
public class InterestTheme extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private TripSokUser user;

	@ManyToOne
	@JoinColumn(name = "theme_id")
	private Theme theme;
}
