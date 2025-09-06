package com.tripsok_back.model.place;

import com.tripsok_back.support.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TAG", indexes = {
	@Index(name = "idx_tag_name", columnList = "NAME")
})
@NoArgsConstructor
public class Tag extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Size(max = 28)
	@Column(name = "NAME", nullable = false, updatable = false, unique = true)
	private String name;

	public Tag(String name) {
		this.name = name;
	}
}
