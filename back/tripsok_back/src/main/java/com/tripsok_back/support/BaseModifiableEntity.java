package com.tripsok_back.support;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class}) // Enables auditing features
@Getter
@Setter
public abstract class BaseModifiableEntity extends BaseTimeEntity {
	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;
}
