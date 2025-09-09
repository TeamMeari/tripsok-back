package com.tripsok_back.model.place;

import com.tripsok_back.support.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE_TAG", indexes = {
	@Index(name = "idx_place_tag_tag", columnList = "TAG_ID"),
	@Index(name = "idx_place_tag_place", columnList = "PLACE_ID"),
},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_place_tag_place_and_tag", columnNames = {"PLACE_ID", "TAG_ID"})
	})
@NoArgsConstructor
public class PlaceTag extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "PLACE_ID", nullable = false)
	private Place place;

	@ManyToOne
	@JoinColumn(name = "TAG_ID", nullable = false)
	private Tag tag;

	public PlaceTag(Place place, Tag tag) {
		this.place = place;
		this.tag = tag;
	}
}
