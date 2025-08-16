package com.tripsok_back.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class TourApiException extends CustomInternalException {
	public TourApiException(InternalErrorCode errorCode) {
		super(errorCode);
		log.error("TourApi Exception occurred:");
	}

}
