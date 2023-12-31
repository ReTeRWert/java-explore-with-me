package ru.ewm.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.service.exception.NotFoundException;
import ru.ewm.service.users.dto.FullUserDto;
import ru.ewm.service.users.dto.NewUserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<FullUserDto> getUsers(List<Long> ids, Long from, Integer size) {

        if (ids.isEmpty()) {
            return getUsersEmpty(size);
        }

        List<User> users = userRepository.findAllById(ids);

        return users.stream()
                .map(UserMapper::toFullUserDto)
                .collect(Collectors.toList());
    }

    private List<FullUserDto> getUsersEmpty(Integer size) {

        PageRequest pageable = PageRequest.of(0, size);

        Page<User> users = userRepository.findAll(pageable);

        return users.stream()
                .map(UserMapper::toFullUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public FullUserDto addUser(NewUserDto newUserDto) {
        User user = UserMapper.toUser(newUserDto);

        return UserMapper.toFullUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new IllegalStateException("User not exist.");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public User getUserIfExist(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.orElseThrow(()
                -> new NotFoundException(userId, User.class.getSimpleName()));
    }
}
