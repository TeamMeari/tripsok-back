package com.tripsok_back.repository.place;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Tag;
import com.tripsok_back.model.place.TagTr;
import com.tripsok_back.type.LocaleCode;

@Repository
public interface TagTrRepository extends JpaRepository<TagTr, Integer> {
	List<TagTr> findByTagInAndLocaleOrderByIdAsc(Set<Tag> tags, LocaleCode localeCode);
}
