package com.tripsok_back.dto.booking.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookingSpotMemoUpdateRequest {
	private Integer bookingSpotId;
	private String memo;
}
