package com.tripsok_back.dto.tourApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourApiIntroResponseDto {

    @JsonProperty("contentid")
    private String contentId;

    @JsonProperty("contenttypeid")
    private String contentTypeId;

    // 개장일
    @JsonProperty("opendate")
    private String openDate;

    // 쉬는날
    @JsonProperty("restdate")
    private String restDate;

    // 이용시간
    @JsonProperty("usetime")
    private String useTime;

    // 음식점(39)
    @JsonProperty("opendatefood")
    private String openDateFood;
    @JsonProperty("restdatefood")
    private String restDateFood;
    @JsonProperty("opentimefood")
    private String openTimeFood;

    // 쇼핑(38)
    @JsonProperty("opendateshopping")
    private String openDateShopping;
    @JsonProperty("restdateshopping")
    private String restDateShopping;
    @JsonProperty("opentime")
    private String openTimeShopping;

    // 숙박(32)
    @JsonProperty("checkintime")
    private String checkInTime;
    @JsonProperty("checkouttime")
    private String checkOutTime;

    // 문화시설(14)
    @JsonProperty("restdateculture")
    private String restDateCulture;
    @JsonProperty("usetimeculture")
    private String useTimeCulture;

    // 레포츠(28)
    @JsonProperty("openperiod")
    private String openPeriodLeports;
    @JsonProperty("restdateleports")
    private String restDateLeports;
    @JsonProperty("usetimeleports")
    private String useTimeLeports;

    // 행사/축제(15)
    @JsonProperty("eventstartdate")
    private String eventStartDate;
    @JsonProperty("eventenddate")
    private String eventEndDate;
    @JsonProperty("usetimefestival")
    private String useTimeFestival;

    // 여행코스(25)
    @JsonProperty("taketime")
    private String takeTime;
}
