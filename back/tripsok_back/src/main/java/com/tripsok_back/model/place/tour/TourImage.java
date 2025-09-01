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
@Table(name = "TOUR_IMAGE", schema = "TRIPSOK")
public class TourImage extends BaseModifiableEntity {
	@Id
	@SequenceGenerator(name = "tour_image_seq", sequenceName = "TOUR_IMAGE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tour_image_seq")
	@Column(name = "ID", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "TOUR_ID", nullable = false)
	private Tour tour;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "ATTRACTION_ID", nullable = false)
	private Attraction attraction;

	@Size(max = 2000)
	@Column(name = "URL", length = 2000)
	private String url;

	public static TourImage buildUrlImage(String image) {
		TourImage tourImage = new TourImage();
		tourImage.setUrl(image);
		return tourImage;
	}
}