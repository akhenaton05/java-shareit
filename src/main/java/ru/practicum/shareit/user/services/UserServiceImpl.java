package ru.practicum.shareit.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).get());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto user) {
        try {
            checkUser(UserMapper.dtoToUser(user));
        } catch (Exception e) {
            throw new ValidateException("Ошибка в запросе");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.dtoToUser(user)));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto user) {
        User oldUser = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        if (Objects.nonNull(user.getName())) {
            oldUser.setName(user.getName());
        }
        if (Objects.nonNull(user.getEmail()) && !oldUser.getEmail().equalsIgnoreCase(user.getEmail())) {
            checkUser(UserMapper.dtoToUser(user));
            oldUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User checkUser(User user) {
        if (Objects.isNull(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new ValidateException("Неверный формат email");
        }
        Optional<User> userToCheck = userRepository.findByEmail(user.getEmail());
        if (userToCheck.isPresent()) {
            throw new ValidateException("Данный email уже используется");
        }
        return user;
    }
}
