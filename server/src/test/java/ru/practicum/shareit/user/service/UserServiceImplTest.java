package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUpdateUser_Success() {

        User user = new User();
        user.setName("Ivan Dorn");
        user.setEmail("ivan.dorn@gmail.com");
        User savedUser = userRepository.save(user);

        User updatedUser = new User();
        updatedUser.setId(savedUser.getId());
        updatedUser.setName("Updated Ivan");
        updatedUser.setEmail("updated.dorn@gmail.com");

        userService.update(updatedUser);

        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals("Updated Ivan", foundUser.getName());
        assertEquals("updated.dorn@gmail.com", foundUser.getEmail());

    }

    @Test
    void testUpdateUser_NotFound() {

        User nonExistentUser = new User();
        nonExistentUser.setId(999L);
        nonExistentUser.setName("Non Existent");

        assertThrows(NotFoundException.class, () -> userService.update(nonExistentUser));
    }
}

