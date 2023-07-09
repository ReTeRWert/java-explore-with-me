package ru.ewm.service.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {

    @NotBlank(message = "Name of the new category should not be blank.")
    @Size(min = 1, max = 50, message = "Name of the category should be 1 to 50 characters.")
    private String name;
}
