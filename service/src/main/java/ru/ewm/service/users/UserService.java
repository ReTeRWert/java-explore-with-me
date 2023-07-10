package ru.ewm.service.users;

import ru.ewm.service.users.dto.FullUserDto;
import ru.ewm.service.users.dto.NewUserDto;

import java.util.List;

public interface UserService {

    List<FullUserDto> getUsers(List<Long> ids, Long from, Integer size);

    FullUserDto addUser(NewUserDto newUserDto);

    User getUserIfExist(Long userId);

    void deleteUser(Long userId);
}
