package ru.practicum.shareit.user;

import java.util.List;

public interface UserDao {
    User createUser(User user);

    Boolean isUserByEmailExists(User user);

    Boolean isEmailUnique(String email, int id);

    Boolean isUserByIdExists(int id);

    User updateUser(User user, int id);

    User getUserById(int id);

    void deleteUser(int id);

    List<User> getAllUsers();
}
