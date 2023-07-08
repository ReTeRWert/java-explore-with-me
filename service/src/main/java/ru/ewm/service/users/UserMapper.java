package ru.ewm.service.users;

import ru.ewm.service.users.dto.FullUserDto;
import ru.ewm.service.users.dto.NewUserDto;
import ru.ewm.service.users.dto.ShortUserDto;

public class UserMapper {

    public static User toUser(NewUserDto newUserDto) {
        User user = new User();

        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());

        return user;
    }

    public static FullUserDto toFullUserDto(User user) {
        return new FullUserDto(
                user.getEmail(),
                user.getId(),
                user.getName()
        );
    }

    public static ShortUserDto toShortUserDto(User user) {
        return new ShortUserDto(
                user.getId(),
                user.getName()
        );
    }
}
