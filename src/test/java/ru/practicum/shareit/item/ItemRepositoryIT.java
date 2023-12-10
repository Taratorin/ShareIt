package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class ItemRepositoryIT {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemRepositoryIT(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        userRepository.save(getUsers().get(0));
        userRepository.save(getUsers().get(1));
        itemRepository.save(getItems().get(0));
        itemRepository.save(getItems().get(1));
    }

    @Test
    void findAllByOwnerIdOrderById_whenOwnerIdCorrect_thenReturnItem() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(1L, Pageable.ofSize(1));
        assertThat(items, equalTo(List.of(getItems().get(0))));
    }

    @Test
    void findAllByOwnerIdOrderById_whenOwnerIdNotCorrect_thenEmptyList() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(-100L, Pageable.ofSize(1));
        assertThat(items, equalTo(List.of()));
    }

    @Test
    void findAllByIsAvailableIsTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase_find2Items() {
        List<Item> items = itemRepository
                .findAllByIsAvailableIsTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase("Описание вЕщи №2", "ВещЬ №1", Pageable.ofSize(10));
        assertThat(items.size(), equalTo(2));
        assertThat(items, equalTo(getItems()));
    }

    @Test
    void findAllByIsAvailableIsTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase_findItems() {
        List<Item> items = itemRepository
                .findAllByIsAvailableIsTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase("Описание вещи №20", "Вещь №10", Pageable.ofSize(10));
        assertThat(items, equalTo(List.of()));
    }

    private List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(
                Item.builder()
                        .id(1)
                        .name("Вещь №1")
                        .description("Описание вещи №1")
                        .isAvailable(true)
                        .owner(getUsers().get(0))
                        .build()
        );
        items.add(
                Item.builder()
                        .id(2)
                        .name("Вещь №2")
                        .description("Описание вещи №2")
                        .isAvailable(true)
                        .owner(getUsers().get(1))
                        .build()
        );
        return items;
    }

    private List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .id(1)
                .name("Владелец вещи №1")
                .email("email1@email.com")
                .build());
        users.add(User.builder()
                .id(2)
                .name("Владелец вещи №2")
                .email("email2@email.com")
                .build());
        return users;
    }

}