package ru.practicum.shareit.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repositories.UserRepositoryImpl;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepositoryImpl userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.showAllUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.getUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto user) {
        return UserMapper.toUserDto(userRepository.addUser(UserMapper.dtoToUser(user)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        return UserMapper.toUserDto(userRepository.updateUser(userId, UserMapper.dtoToUser(user)));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
