package com.tripsok_back.dto.tourApi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourApiPlaceDetailResponseDto {

	private String contentId;                // 콘텐츠 ID
	private String contentTypeId;            // 콘텐츠 타입 ID
	private String title;                    // 명칭
	private String telephone;                // 전화번호
	private String telephoneName;
	private String homepage;
	private String firstImageUrl;            // 대표 이미지 URL
	private String firstImageUrlSecondary;   // 보조 이미지 URL
	private String copyrightDivisionCode;    // 저작권 구분 코드
	private String areaCode;                 // 지역 코드
	private String sigunguCode;              // 시군구 코드
	private String legalDongRegionCode;      // 법정동 지역 코드
	private String legalDongSignguCode;      // 법정동 시군구 코드
	private String largeClassificationSystem1; // 대분류 체계 1
	private String largeClassificationSystem2; // 대분류 체계 2
	private String largeClassificationSystem3; // 대분류 체계 3
	private String categoryLevel1;           // 카테고리 1
	private String categoryLevel2;           // 카테고리 2
	private String categoryLevel3;           // 카테고리 3
	private String address1;                 // 기본 주소
	private String address2;                 // 상세 주소
	private String zipcode;                  // 우편번호
	private String mapX;                     // 지도 X좌표 (경도, Longitude)
	private String mapY;                     // 지도 Y좌표 (위도, Latitude)
	private String mapLevel;                 // 지도 확대 Level
	private String overview;                 // 상세 설명
	private String createdTime;              // 등록일시
	private String modifiedTime;             // 수정일시
}
