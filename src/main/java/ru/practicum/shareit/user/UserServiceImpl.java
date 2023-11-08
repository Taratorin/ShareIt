package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private int id;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() != null) {
            User user = UserMapper.toUser(userDto);
            if (!userDao.isUserByEmailExists(user)) {
                user.setId(getId());
                User userFromDao = userDao.createUser(user);
                return UserMapper.toUserDto(userFromDao);
            } else {
                throw new ConflictException("Пользователь с такой почтой уже существует.");
            }
        } else {
            throw new BadRequestException("Не указана почта пользователя.");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        if (userDao.isUserByIdExists(id)) {
            User user = userDao.getUserById(id);
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                if (!userDao.isEmailUnique(userDto.getEmail(), id)) {
                    throw new ConflictException("Пользователь с такой почтой уже существует.");
                }
                user.setEmail(userDto.getEmail());
            }
            User userFromDao = userDao.updateUser(user, id);
            return UserMapper.toUserDto(userFromDao);
        } else {
            throw new BadRequestException("Пользователь с id=" + id + " не существует.");
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userDao.getAllUsers();
        return allUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(int id) {
        User user = userDao.getUserById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(int id) {
        if (userDao.isUserByIdExists(id)) {
            userDao.deleteUser(id);
        } else {
            throw new BadRequestException("Пользователь с id=" + id + " не существует.");
        }
    }

    private int getId() {
        return ++id;
    }
}
