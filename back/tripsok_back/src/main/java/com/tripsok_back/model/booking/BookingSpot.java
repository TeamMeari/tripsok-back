package com.tripsok_back.model.booking;

import java.math.BigDecimal;

import com.tripsok_back.model.tripplan.VisitSpot;
import com.tripsok_back.type.LocaleCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class BookingSpot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "BOOKING_ID", nullable = false, updatable = false)
	private Booking booking;
	@Column(name = "PLACE_ID", nullable = false, updatable = false)
	private Integer placeId;
	@Column(name = "PLACE_NAME", nullable = false, updatable = false)
	private String placeName;
	@Column(name = "ADDRESS", nullable = false, updatable = false)
	private String address;
	@Column(name = "LATITUDE", nullable = false, updatable = false)
	private BigDecimal latitude;
	@Column(name = "LONGITUDE", nullable = false, updatable = false)
	private BigDecimal longitude;
	@Column(name = "ORDER_INDEX", nullable = false, updatable = false)
	private Integer orderIndex;
	@Column(name = "MEMO")
	private String memo;

	public BookingSpot(Booking booking, VisitSpot visitSpot, LocaleCode locale) {
		this.booking = booking;
		this.placeId = visitSpot.getPlace().getId();
		this.placeName = visitSpot.getPlace().getPlaceTr(locale).getPlaceName();
		this.address = visitSpot.getPlace().getPlaceTr(locale).getAddress();
		this.latitude = visitSpot.getPlace().getMapY();
		this.longitude = visitSpot.getPlace().getMapX();
		this.orderIndex = visitSpot.getOrderIndex();
		this.memo = visitSpot.getMemo();
	}

	public void updateMemo(String memo) {
		this.memo = memo;
	}
}
