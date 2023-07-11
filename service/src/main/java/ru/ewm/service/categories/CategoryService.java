package ru.ewm.service.categories;

import ru.ewm.service.categories.dto.CategoryDto;
import ru.ewm.service.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto newCategory);

    CategoryDto updateCategory(Long id, NewCategoryDto updateCategory);

    void deleteCategory(Long id);

    List<CategoryDto> getAllCategories(Long from, Integer size);

    CategoryDto getCategoryById(Long id);

    Category getCategoryIfExist(Long catId);
}
