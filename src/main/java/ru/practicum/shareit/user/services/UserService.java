package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    void deleteUser(Long userId);
}
