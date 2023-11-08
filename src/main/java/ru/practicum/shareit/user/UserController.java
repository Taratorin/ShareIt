package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users — добавление пользователя");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable int id) {
        log.info("Получен запрос PUT /users — обновление пользователя");
        return userService.updateUser(userDto, id);
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос GET /users — получение пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUsers(@PathVariable int id) {
        log.info("Получен запрос GET /users/{Id} — получение пользователя по id");
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Получен запрос DELETE /users/{userId} — удаление пользователя");
        userService.deleteUser(id);
    }

}
