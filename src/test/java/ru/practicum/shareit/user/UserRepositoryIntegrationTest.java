package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryIntegrationTest {
    private UserRepository userRepository;

    @Autowired
    public UserRepositoryIntegrationTest(UserRepository userRepository) {
        this.userRepository = userRepository;
        userRepository.save(getUsersList().get(0));
        userRepository.save(getUsersList().get(1));
        userRepository.save(getUsersList().get(2));
    }

    @Test
    void findById_whenCorrectId_thenUserPresent() {
        Optional<User> user = userRepository.findById(1L);
        assertTrue(user.isPresent());
    }

    @Test
    void findById_whenNotCorrectId_thenUserAbsent() {
        Optional<User> user = userRepository.findById(-100L);
        assertTrue(user.isEmpty());
    }

    @Test
    void findAll() {
        List<User> users = userRepository.findAll();
        assertThat(users, equalTo(getUsersList()));
    }


    private List<User> getUsersList() {
        List<User> users = new ArrayList<>();
        users.add(
                User.builder()
                        .id(1)
                        .name("Пользователь 1")
                        .email("email1@email.com")
                        .build()
        );
        users.add(
                User.builder()
                        .id(2)
                        .name("Пользователь 2")
                        .email("email2@email.com")
                        .build()
        );
        users.add(
                User.builder()
                        .id(3)
                        .name("Пользователь 3")
                        .email("email3@email.com")
                        .build()
        );
        return users;
    }
}