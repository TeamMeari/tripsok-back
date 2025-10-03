package com.tripsok_back.dto.booking.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
	@Schema(description = "사용자의 contact email", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "contactEmail은 필수입니다.")
	private String contactEmail;
	@Schema(description = "사용자의 이름", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "userName은 필수입니다.")
	private String userName;
	@Schema(description = "결제 정보", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "paymentInfo는 필수입니다.")
	private PaymentInfo paymentInfo;
}
