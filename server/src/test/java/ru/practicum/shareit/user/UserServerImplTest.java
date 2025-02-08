package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.user.services.UserServiceImpl;


import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServerImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto dto;
    private User user = new User();
    private User updatedUser = new User();

    @BeforeEach
    void setUp() {
        dto = makeUserDto("email@email.ru", "name");
        user = makeUser(1L, "email@email.ru", "name");
        updatedUser = makeUser(1L, "nee@email.ru", "nee");
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto createdUser = userService.createUser(dto);

        assertEquals(1L, createdUser.getId());
        assertEquals(dto.getEmail(), createdUser.getEmail());
        assertEquals(dto.getName(), createdUser.getName());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserWithWrongEmail() {
        dto.setEmail("dsa asd");
        Exception exception = assertThrows(ValidateException.class, () -> userService.createUser(dto));

        String expectedMessage = "Ошибка в запросе";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserWithExistingEmail() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.ofNullable(user));

        Exception exception = assertThrows(ValidateException.class, () -> userService.createUser(dto));

        String expectedMessage = "Ошибка в запросе";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> userList = userService.getUsers();

        assertEquals(userList.size(), 1);
        assertEquals(userList.getFirst().getId(), 1L);
        assertEquals(userList.getFirst().getEmail(), user.getEmail());
        assertEquals(userList.getFirst().getName(), user.getName());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UserDto userRes = userService.getUserById(1L);

        assertEquals(1L, userRes.getId());
        assertEquals(user.getEmail(), userRes.getEmail());
        assertEquals(user.getName(), userRes.getName());
    }

    @Test
    void updateUserWrongId() {
        long wrongId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(wrongId, dto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser() {
        user.setEmail("newe@email.ru");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        UserDto upUser = userService.updateUser(2L, dto);

        assertEquals(1L, upUser.getId());
        assertEquals(upUser.getEmail(), updatedUser.getEmail());
        assertEquals(upUser.getName(), updatedUser.getName());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserWithNoEmail() {
        updatedUser.setEmail(null);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        UserDto upUser = userService.updateUser(2L, dto);

        assertEquals(1L, upUser.getId());
        assertEquals(upUser.getEmail(), updatedUser.getEmail());
        assertEquals(upUser.getName(), updatedUser.getName());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(user.getId());
        verify(userRepository).deleteById(user.getId());
    }


    private UserDto makeUserDto(String email, String name) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);

        return dto;
    }

    private User makeUser(Long id, String email, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);

        return user;
    }
}
