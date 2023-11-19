package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ModelMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.map(userDto, User.class);
        if (!userDao.isUserByEmailExists(user)) {
            User userFromDao = userDao.createUser(user);
            return mapper.map(userFromDao, UserDto.class);
        } else {
            throw new ConflictException("Пользователь с такой почтой уже существует.");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        User user = getUserById(id);
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            if (!userDao.isEmailUnique(email, id)) {
                throw new ConflictException("Пользователь с такой почтой уже существует.");
            }
            user.setEmail(email);
        }
        return mapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userDao.getAllUsers();
        return allUsers.stream()
                .map(x -> mapper.map(x, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDtoById(int id) {
        User user = getUserById(id);
        return mapper.map(user, UserDto.class);
    }

    @Override
    public void deleteUser(int id) {
        if (userDao.isUserByIdExists(id)) {
            userDao.deleteUser(id);
        } else {
            throw new BadRequestException("Пользователь с id=" + id + " не существует.");
        }
    }

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не существует."));
    }

}
