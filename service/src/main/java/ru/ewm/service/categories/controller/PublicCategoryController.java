package ru.ewm.service.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.categories.CategoryService;
import ru.ewm.service.categories.dto.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
@Validated
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Long from,
                                              @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("get all categories.");

        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("get category by id: {}", catId);

        return categoryService.getCategoryById(catId);
    }

}
