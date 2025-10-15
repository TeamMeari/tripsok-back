package com.tripsok_back.dto.booking.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInfo {
	@Schema(description = "결제 고유 번호", example = "MC42NTA1NDEzODU5Mjly")
	@NotNull
	private String orderNumber;
	@Schema(description = "결제 금액", example = "30000")
	@NotNull
	private Integer amount;
	@Schema(description = "paymentKey", example = "tgen_20250918155703IfA80")
	@NotNull
	private String paymentKey;
}
