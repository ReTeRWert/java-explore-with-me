package ru.ewm.service.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.service.categories.dto.CategoryDto;
import ru.ewm.service.categories.dto.NewCategoryDto;
import ru.ewm.service.events.EventRepository;
import ru.ewm.service.exception.InvalidOperationException;
import ru.ewm.service.util.ExistValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final ExistValidator existValidator;


    @Override
    public CategoryDto addCategory(NewCategoryDto newCategory) {
        Category category = CategoryMapper.toCategory(newCategory);

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto updateCategory) {
        Category category = existValidator.getCategoryIfExist(catId);

        category.setName(updateCategory.getName());

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        existValidator.getCategoryIfExist(catId);

        if (eventRepository.findEventByCategoryId(catId).size() != 0) {
            throw new InvalidOperationException("Cannot delete category with events.");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getAllCategories(Long from, Integer size) {
        int startPage = Math.toIntExact(from / size);

        Page<Category> categories = categoryRepository.findAll(PageRequest.of(startPage, size));

        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = existValidator.getCategoryIfExist(id);

        return CategoryMapper.toCategoryDto(category);
    }
}
