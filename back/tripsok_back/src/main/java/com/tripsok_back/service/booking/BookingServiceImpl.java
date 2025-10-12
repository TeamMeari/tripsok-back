package com.tripsok_back.service.booking;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripsok_back.dto.booking.request.BookingRequest;
import com.tripsok_back.dto.booking.request.BookingSpotMemoUpdateRequest;
import com.tripsok_back.dto.booking.response.CompleteBookingResponse;
import com.tripsok_back.exception.BookingException;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.model.booking.Booking;
import com.tripsok_back.model.booking.BookingSpot;
import com.tripsok_back.model.tripplan.TripPlan;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.booking.BookingRepository;
import com.tripsok_back.repository.booking.BookingSpotRepository;
import com.tripsok_back.service.email.EmailService;
import com.tripsok_back.service.tripplan.TripPlanService;
import com.tripsok_back.service.user.UserService;
import com.tripsok_back.type.LocaleCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final UserService userService;
	private final EmailService emailService;
	private final TripPlanService tripPlanService;
	private final BookingRepository bookingRepository;
	private final BookingSpotRepository bookingSpotRepository;

	@Override
	public void validateBookingTripPlan(Integer userId) {
		userService.findUserById(userId);
		TripPlan tripPlan = tripPlanService.findDraftTripPlanByUserId(userId);
		validateBookingRequest(tripPlan);
	}

	@Override
	@Transactional
	public CompleteBookingResponse completeBooking(Integer userId, LocaleCode locale, BookingRequest request) {
		TripSokUser user = userService.findUserById(userId);
		TripPlan tripPlan = tripPlanService.findDraftTripPlanByUserId(userId);
		Booking booking = new Booking(user, request, locale, tripPlan);
		bookingRepository.save(booking);
		tripPlan.setStatus(TripPlan.PlanStatus.COMPLETED);
		Set<BookingSpot> bookingSpotSet = tripPlan.getVisitSpotSet().stream().map(visitSpot ->
			new BookingSpot(booking, visitSpot, locale)
		).collect(Collectors.toSet());
		booking.setBookingSpotSet(bookingSpotSet);
		emailService.sendBookingConfirmationEmail(request, request.getUserName(), tripPlan);
		return new CompleteBookingResponse(request.getContactEmail());
	}

	@Override
	@Transactional
	public void UpdateBookingSpotMemo(Integer userId, BookingSpotMemoUpdateRequest request) {
		TripSokUser user = userService.findUserById(userId);
		bookingSpotRepository.findByIdAndBooking_User(request.getBookingSpotId(), user)
			.orElseThrow(() -> new BookingException(ErrorCode.BOOKING_SPOT_NOT_FOUND))
			.updateMemo(request.getMemo());
	}

	private void validateBookingRequest(TripPlan tripPlan) {
		if (tripPlan == null) {
			throw new BookingException(ErrorCode.NOT_FOUND_BOOKING_TRIP_PLAN);
		}
		if (tripPlan.getNumberOfPeople() == null || tripPlan.getVisitSpotSet().isEmpty()
			|| tripPlan.getStartTime() == null || tripPlan.getTripDate() == null) {
			throw new BookingException(ErrorCode.INCOMPLETE_TRIP_PLAN);
		}
		if (!tripPlan.getTripDate().isAfter(LocalDate.now())) {
			throw new BookingException(ErrorCode.BOOKING_PAST_TRIP_DATE);
		}
	}
}
