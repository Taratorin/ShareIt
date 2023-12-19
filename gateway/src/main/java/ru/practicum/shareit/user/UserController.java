package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Creating user on POST /users {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Validated(Update.class) @RequestBody UserDto userDto,
                                             @PathVariable int userId) {
        log.info("Update user on PATCH /users {}, userId={}", userDto, userId);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> findAllUsers() {
        log.info("Update user on GET /users");
        return userClient.findAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUser(@PathVariable int userId) {
        log.info("Get user by id on GET /users, userId={}", userId);
        return userClient.findUserDtoById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Delete user by id on DELETE /users, userId={}", userId);
        userClient.deleteUser(userId);
    }

}