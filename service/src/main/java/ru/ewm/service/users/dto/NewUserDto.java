package ru.ewm.service.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;

    private Long id;

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
