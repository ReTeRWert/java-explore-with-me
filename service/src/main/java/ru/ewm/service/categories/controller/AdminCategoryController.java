package ru.ewm.service.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.categories.CategoryService;
import ru.ewm.service.categories.dto.CategoryDto;
import ru.ewm.service.categories.dto.NewCategoryDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoryController {

    private final CategoryService adminCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @NotNull @Valid NewCategoryDto newCategoryDto) {

        log.info("Adding new category: {}", newCategoryDto);

        return adminCategoryService.addCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody @NotNull @Valid NewCategoryDto updateCategory) {

        log.info("Updating category: {}", updateCategory);

        return adminCategoryService.updateCategory(catId, updateCategory);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {

        log.info("Deleting category with id: {}", catId);

        adminCategoryService.deleteCategory(catId);
    }
}
