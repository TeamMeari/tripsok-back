package com.tripsok_back.service.place;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.place.PlaceTagResponseDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTag;
import com.tripsok_back.model.place.Tag;
import com.tripsok_back.model.place.TagTr;
import com.tripsok_back.repository.place.TagTrRepository;
import com.tripsok_back.type.LocaleCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
	private final TagTrRepository tagTrRepository;

	@Override
	public Set<PlaceTagResponseDto> getPlaceTags(Place place, LocaleCode locale) {
		if (locale == LocaleCode.KO) {
			return place.getTags().stream().map(PlaceTag::toDto).collect(Collectors.toSet());
		} else {
			Set<Tag> tags = place.getTags().stream().map(PlaceTag::getTag).collect(Collectors.toSet());
			List<TagTr> tagTrs = tags.stream().map(it-> it.getTagTr(locale)).toList();
			return tagTrs.stream()
				.map(it -> new PlaceTagResponseDto(it.getTag().getId(), it.getName()))
				.collect(Collectors.toSet());
		}
	}
}
