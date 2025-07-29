package com.tripsok_back.model.place;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ACCOMODATION")
public class Accomodation {
	@Id
	@Column(name = "ACCOMODATION_ID", nullable = false)
	private Long id;

	@Size(max = 255)
	@Column(name = "ACCOMODATION_TYPE")
	private String accomodationType;

	@Column(name = "CREATED_AT")
	private Instant createdAt;

}