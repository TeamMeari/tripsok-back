package com.tripsok_back.model.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.tripsok_back.dto.booking.request.BookingRequest;
import com.tripsok_back.model.tripplan.TripPlan;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.support.BaseTimeEntity;
import com.tripsok_back.type.LocaleCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
	@Index(name = "idx_booking_user_id", columnList = "user_id"),
})
public class Booking extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private TripSokUser user;

	@Column(name = "CONTACT_EMAIL", nullable = false)
	private String contactEmail;

	@Column(name = "USER_NAME", nullable = false)
	private String userName;

	@Column(name = "TRIP_DATE", nullable = false)
	private LocalDate tripDate;

	@Column(name = "START_TIME", nullable = false)
	private LocalTime startTime;

	@Column(name = "NUMBER_OF_PEOPLE", nullable = false)
	private Integer numberOfPeople;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BookingSpot> bookingSpotSet = new HashSet<>();

	@Column(name = "ORDER_NUMBER", unique = true, nullable = false)
	private String orderNumber;

	@Column(name = "AMOUNT", nullable = false)
	private Integer amount;

	@Column(name = "PAYMENT_KEY", unique = true, nullable = false)
	private String paymentKey;

	@Enumerated(EnumType.STRING)
	@Column(name = "LOCALE", nullable = false, length = 20)
	private LocaleCode locale;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false, length = 20)
	private BookingStatus status = BookingStatus.before_travel;

	public Booking(TripSokUser user, BookingRequest request, LocaleCode locale, TripPlan tripPlan) {
		this.user = user;
		this.contactEmail = request.getContactEmail();
		this.userName = request.getUserName();
		this.tripDate = tripPlan.getTripDate();
		this.startTime = tripPlan.getStartTime();
		this.numberOfPeople = tripPlan.getNumberOfPeople();
		this.orderNumber = request.getPaymentInfo().getOrderNumber();
		this.amount = request.getPaymentInfo().getAmount();
		this.paymentKey = request.getPaymentInfo().getPaymentKey();
		this.locale = locale;
	}

	@Getter
	public enum BookingStatus {
		traveling("여행중"),
		completed("여행완료"),
		canceled("취소됨"),
		before_travel("여행전");

		private final String description;

		BookingStatus(String description) {
			this.description = description;
		}
	}

}
