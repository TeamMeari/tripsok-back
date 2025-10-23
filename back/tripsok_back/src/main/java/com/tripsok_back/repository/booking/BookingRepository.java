package com.tripsok_back.repository.booking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.booking.Booking;
import com.tripsok_back.model.user.TripSokUser;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
	List<Booking> findAllByUserOrderByTripDateDesc(TripSokUser user);

	Booking findTopByUserOrderByCreatedAtDesc(TripSokUser user);
}
