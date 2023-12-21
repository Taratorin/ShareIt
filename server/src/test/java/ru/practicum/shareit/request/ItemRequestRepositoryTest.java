//package ru.practicum.shareit.request;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Pageable;
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
//class ItemRequestRepositoryTest {
//
//    private final ItemRequestRepository itemRequestRepository;
//
//    @Autowired
//    public ItemRequestRepositoryTest(ItemRequestRepository itemRequestRepository, UserRepository userRepository) {
//        this.itemRequestRepository = itemRequestRepository;
//        userRepository.save(getUsers().get(0));
//        userRepository.save(getUsers().get(1));
//        itemRequestRepository.save(getRequests().get(0));
//        itemRequestRepository.save(getRequests().get(1));
//    }
//
//    @Test
//    void findAllByRequestorOrderByCreatedDesc_whenFound() {
//        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(getUsers().get(0));
//        assertThat(requests, equalTo(getRequests()));
//    }
//
//    @Test
//    void findAllByRequestorOrderByCreatedDesc_whenNotFound() {
//        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(getUsers().get(1));
//        assertThat(requests, equalTo(List.of()));
//    }
//
//    @Test
//    void findAllByRequestorNotOrderByCreatedDesc_whenNotFound() {
//        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(getUsers().get(0), Pageable.ofSize(10));
//        assertThat(requests, equalTo(List.of()));
//    }
//
//    @Test
//    void findAllByRequestorNotOrderByCreatedDesc_whenFound() {
//        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(getUsers().get(1), Pageable.ofSize(10));
//        assertThat(requests, equalTo(getRequests()));
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
//    private List<ItemRequest> getRequests() {
//        List<ItemRequest> requests = new ArrayList<>();
//        requests.add(
//                ItemRequest.builder()
//                        .id(1)
//                        .description("I need a thing!")
//                        .requestor(getUsers().get(0))
//                        .build()
//        );
//        requests.add(
//                ItemRequest.builder()
//                        .id(2)
//                        .description("I need another thing!")
//                        .requestor(getUsers().get(0))
//                        .build()
//        );
//        return requests;
//    }
//
//}