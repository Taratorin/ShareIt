package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users — добавление пользователя");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable int id) {
        log.info("Получен запрос PATCH /users — обновление пользователя");
        return userService.updateUser(userDto, id);
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос GET /users — получение пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) {
        log.info("Получен запрос GET /users/{Id} — получение пользователя по id");
        return userService.getUserDtoById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Получен запрос DELETE /users/{userId} — удаление пользователя");
        userService.deleteUser(id);
    }

}
