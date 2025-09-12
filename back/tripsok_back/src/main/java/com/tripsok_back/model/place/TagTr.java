package com.tripsok_back.model.place;

import com.tripsok_back.support.BaseTimeEntity;
import com.tripsok_back.type.LocaleCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TAG_TR", indexes = {
	@Index(name = "idx_tag_tr_tag_and_locale", columnList = "TAG_ID, LOCALE"),
	@Index(name = "idx_place_tag_name", columnList = "NAME"),
},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_place_tag_tag_and_locale", columnNames = {"TAG_ID", "LOCALE"})
	})
@NoArgsConstructor
public class TagTr extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "TAG_ID", nullable = false)
	private Tag tag;

	@NotNull
	@Column(name = "LOCALE", nullable = false, length = 20)
	private LocaleCode locale;

	@Size(max = 100)
	@Column(name = "NAME", nullable = false, updatable = false)
	private String name;

	public TagTr(Tag tag, LocaleCode locale, String name) {
		this.tag = tag;
		this.locale = locale;
		this.name = name;
	}
}
