package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserDaoInMemory implements UserDao {

    private final Map<Integer, User> users;
    private int id;

    @Override
    public User createUser(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user, int id) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Boolean isUserByEmailExists(User user) {
        String email = user.getEmail();
        List<User> list = users.values().stream()
                .filter(x -> x.getEmail().equals(email))
                .collect(Collectors.toList());
        return !list.isEmpty();
    }

    @Override
    public Boolean isEmailUnique(String email, int id) {
        List<User> list = users.values().stream()
                .filter(x -> (x.getEmail().equals(email) && x.getId() != id))
                .collect(Collectors.toList());
        return list.isEmpty();
    }

    @Override
    public Boolean isUserByIdExists(int id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.of(users.get(id));
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private int getId() {
        return ++id;
    }
}
