package ru.ewm.service.categories;

import ru.ewm.service.categories.dto.CategoryDto;
import ru.ewm.service.categories.dto.NewCategoryDto;

public class CategoryMapper {

    public static Category toCategory(NewCategoryDto categoryDto) {
        return new Category(
                categoryDto.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName());
    }
}
