package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static ru.practicum.shareit.config.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users — добавление пользователя");
        return userService.saveUser(userDto);
    }

    @PatchMapping()
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос PATCH /users — обновление пользователя");
        return userService.updateUser(userDto, userId);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.info("Получен запрос GET /users — получение пользователей");
        return userService.findAllUsers();
    }

    @GetMapping(headers = X_SHARER_USER_ID)
    public UserDto findUser(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос GET /users/{userId} — получение пользователя по id");
        return userService.findUserDtoById(userId);
    }

    @DeleteMapping()
    public void deleteUser(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос DELETE /users/{userId} — удаление пользователя");
        userService.deleteUser(userId);
    }
}