package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.handler.exception.ConflictException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "test@example.com");
        userDto = new UserDto(1L, "Test User", "test@example.com");
    }

    @Test
    void testCreate_Success() {

        when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(user);

        UserDto createdUser = userService.create(user);

        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        verify(repository, times(1)).save(user);
    }

    @Test
    void testCreate_EmailAlreadyExists_ThrowsConflictException() {

        when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(user));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void testUpdate_Success_UpdateNameAndEmail() {

        User updatedUser = new User(1L, "Updated Name", "updated@example.com");
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(updatedUser);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void testUpdate_Success_UpdateOnlyName() {

        User updatedUser = new User(1L, "Updated Name", null);
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(new User(1L, "Updated Name", "test@example.com"));

        UserDto result = userService.update(updatedUser);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testUpdate_UserNotFound_ThrowsNotFoundException() {

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(new User(999L, "Name", "email@test.com")));
        verify(repository, never()).save(any(User.class));
    }


    @Test
    void testGetById_Success() {

        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto foundUser = userService.getById(user.getId());

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void testGetById_UserNotFound_ThrowsNotFoundException() {

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    void testGetAll_ReturnsListOfUsers() {

        List<User> users = List.of(user, new User(2L, "User 2", "user2@example.com"));
        when(repository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAll_ReturnsEmptyList() {

        when(repository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDelete_Success() {

        userService.delete(user.getId());

        verify(repository, times(1)).deleteById(user.getId());
    }
}