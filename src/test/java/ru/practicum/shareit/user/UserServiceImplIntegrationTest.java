package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl service;
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DirtiesContext
    void findAllUsers() {
        List<User> users = easyRandom.objects(User.class, 3).collect(Collectors.toList());
        User user1 = userRepository.save(users.get(0));
        User user2 = userRepository.save(users.get(1));
        User user3 = userRepository.save(users.get(2));

        List<UserDto> userDtoExpected = List.of(UserMapper.toUserDto(user1), UserMapper.toUserDto(user2), UserMapper.toUserDto(user3));

        List<UserDto> userDtos = service.findAllUsers();
        assertThat(userDtos, equalTo(userDtoExpected));
    }
}