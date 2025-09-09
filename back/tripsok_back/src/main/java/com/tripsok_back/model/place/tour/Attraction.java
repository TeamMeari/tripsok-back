package com.tripsok_back.model.place.tour;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.tripsok_back.support.BaseModifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ATTRACTION")
public class Attraction extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "attraction_seq", sequenceName = "ATTRACTION_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attraction_seq")
	@Column(name = "ID", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "TOUR_ID", nullable = false)
	private Tour tour;

	@Size(max = 255)
	@Column(name = "ATTRACTION_NAME")
	private String attractionName;

	@Size(max = 255)
	@Column(name = "ATTRACTION_PRICE")
	private String attractionPrice;

	@Size(max = 255)
	@Column(name = "ATTRACTION_INFORMATION")
	private String attractionInformation;

}
