package ru.practicum.shareit.user.repositories;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> showAllUsers();

    User addUser(User user);

    User updateUser(Long userId, User user);

    User checkUser(User user);

    User getUserById(Long id);

    long getNextId();
}
