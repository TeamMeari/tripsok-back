package com.tripsok_back.support;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass //부모 클래스에 선언하고 속성만 상속 받아서 사용하고 싶을 때 @MappedSuperclass를 사용(엔티티로 선언 X)
@EntityListeners({AuditingEntityListener.class})//엔티티 삽입, 삭제 등의 작업 전 후에 이벤트 처리를 위한 어노테이션
@Setter
@Getter
public abstract class BaseTimeEntity {
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
}
