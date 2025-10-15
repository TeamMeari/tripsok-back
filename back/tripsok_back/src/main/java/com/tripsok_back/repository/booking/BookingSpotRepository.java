package com.tripsok_back.repository.booking;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.booking.BookingSpot;
import com.tripsok_back.model.user.TripSokUser;

@Repository
public interface BookingSpotRepository extends JpaRepository<BookingSpot, Integer> {
	Optional<BookingSpot> findByIdAndBooking_User(Integer bookingSpotId, TripSokUser user);
}
