package ru.practicum.shareit.user.repositories;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {
    @Getter
    private final Map<Long, User> users = new HashMap<>();

    public List<User> showAllUsers() {
        return new ArrayList<User>(users.values());
    }

    public User addUser(User user) {
        try {
            checkUser(user);
        } catch (Exception e) {
            throw new ValidateException("Ошибка в запросе");
        }
        User newUser = new User(getNextId(), user.getEmail(), user.getName());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь {} был успешно добавлен", newUser);
        return newUser;
    }

    public User updateUser(Long userId, User user) {
        if (users.containsKey(userId)) {
            User oldUser = users.get(userId);
            oldUser.setName(user.getName());
            if (user.getEmail() != null && !oldUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                checkUser(user);
                oldUser.setEmail(user.getEmail());
            }
            users.put(oldUser.getId(), oldUser);
            log.info("Пользователь {} был успешно обновлен", oldUser);
            return oldUser;
        }
        throw new ValidateException("User с ID " + user.getId() + " не найден");
    }

    public User getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        throw new NotFoundException("Юзера с ID " + userId + " не существует");
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public User checkUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidateException("Неверный формат email");
        }
        for (User u : users.values()) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new ValidateException("Данный email уже используется");
            }
        }
        return user;
    }

    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}