package com.tripsok_back.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.booking.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
}
