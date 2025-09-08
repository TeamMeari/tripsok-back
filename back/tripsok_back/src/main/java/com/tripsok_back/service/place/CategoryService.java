package com.tripsok_back.service.place;

import com.tripsok_back.model.place.PlaceLclsCategory;

public interface CategoryService {

	void requestAndUpdateCategory();

	PlaceLclsCategory getCategoryByCode(String code);
}
