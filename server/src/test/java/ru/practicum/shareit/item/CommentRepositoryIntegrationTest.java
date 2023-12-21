//package ru.practicum.shareit.item;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import ru.practicum.shareit.user.User;
//import ru.practicum.shareit.user.UserRepository;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.MatcherAssert.assertThat;
//
//@DataJpaTest
//class CommentRepositoryIntegrationTest {
//
//    private final CommentRepository commentRepository;
//
//
//    @Autowired
//    public CommentRepositoryIntegrationTest(CommentRepository commentRepository, UserRepository userRepository, ItemRepository itemRepository) {
//        this.commentRepository = commentRepository;
//        userRepository.save(getUsers().get(0));
//        userRepository.save(getUsers().get(1));
//        itemRepository.save(getItems().get(0));
//        itemRepository.save(getItems().get(1));
//        commentRepository.save(getComments().get(0));
//        commentRepository.save(getComments().get(1));
//    }
//
//    @Test
//    void findAllByItem_whenFound() {
//        List<Comment> comments = commentRepository.findAllByItem(getItems().get(0));
//        assertThat(comments, equalTo(getComments()));
//    }
//
//    @Test
//    void findAllByItem_whenNotFound() {
//        List<Comment> comments = commentRepository.findAllByItem(getItems().get(1));
//        assertThat(comments, equalTo(List.of()));
//    }
//
//    @Test
//    void findAllByItemIn() {
//        List<Comment> comments = commentRepository.findAllByItemIn(getItems());
//        assertThat(comments, equalTo(getComments()));
//    }
//
//    private List<Item> getItems() {
//        List<Item> items = new ArrayList<>();
//        items.add(
//                Item.builder()
//                        .id(1)
//                        .name("Вещь №1")
//                        .description("Описание вещи №1")
//                        .isAvailable(true)
//                        .owner(getUsers().get(0))
//                        .build()
//        );
//        items.add(
//                Item.builder()
//                        .id(2)
//                        .name("Вещь №2")
//                        .description("Описание вещи №2")
//                        .isAvailable(true)
//                        .owner(getUsers().get(1))
//                        .build()
//        );
//        return items;
//    }
//
//    private List<User> getUsers() {
//        List<User> users = new ArrayList<>();
//        users.add(User.builder()
//                .id(1)
//                .name("Владелец вещи №1")
//                .email("email1@email.com")
//                .build());
//        users.add(User.builder()
//                .id(2)
//                .name("Владелец вещи №2")
//                .email("email2@email.com")
//                .build());
//        return users;
//    }
//
//    private List<Comment> getComments() {
//        List<Comment> comments = new ArrayList<>();
//        comments.add(
//                Comment.builder()
//                        .id(1)
//                        .text("Хорошая вещь")
//                        .item(getItems().get(0))
//                        .author(getUsers().get(0))
//                        .build()
//        );
//        comments.add(
//                Comment.builder()
//                        .id(2)
//                        .text("Всем рекомендую")
//                        .item(getItems().get(0))
//                        .author(getUsers().get(1))
//                        .build()
//        );
//        return comments;
//    }
//}