package ru.ewm.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.users.dto.FullUserDto;
import ru.ewm.service.users.dto.NewUserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<FullUserDto> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size) {

        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullUserDto addUser(@RequestBody @NotNull @Valid NewUserDto newUserDto) {
        return userService.addUser(newUserDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
