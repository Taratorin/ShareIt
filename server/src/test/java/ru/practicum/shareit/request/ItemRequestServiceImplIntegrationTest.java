package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

@SpringBootTest
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestServiceImpl service;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private CommentRepository commentRepository;
    private final EasyRandom easyRandom = new EasyRandom();


//    @Test
//    @DirtiesContext
//    public void findItemRequests() {
//        User user1 = easyRandom.nextObject(User.class);
//        User user2 = easyRandom.nextObject(User.class);
//        user1 = userRepository.save(user1);
//        user2 = userRepository.save(user2);
//
//        ItemRequest itemRequest1 = easyRandom.nextObject(ItemRequest.class);
//        itemRequest1.setRequestor(user1);
//        itemRequest1.setCreated(null);
//        itemRequest1 = requestRepository.save(itemRequest1);
//        ItemRequest itemRequest2 = easyRandom.nextObject(ItemRequest.class);
//        itemRequest2.setRequestor(user2);
//        itemRequest2.setCreated(null);
//        itemRequest2 = requestRepository.save(itemRequest2);
//
//        Item item1 = easyRandom.nextObject(Item.class);
//        item1.setOwner(user1);
//        item1.setRequest(itemRequest1);
//        item1 = itemRepository.save(item1);
//        Item item2 = easyRandom.nextObject(Item.class);
//        item2.setOwner(user1);
//        item2.setRequest(itemRequest2);
//        item2 = itemRepository.save(item2);
//
//        Comment comment1 = easyRandom.nextObject(Comment.class);
//        Comment comment2 = easyRandom.nextObject(Comment.class);
//        comment1.setItem(item1);
//        comment2.setItem(item2);
//        comment1.setAuthor(user1);
//        comment2.setAuthor(user2);
//        comment1.setCreated(null);
//        comment2.setCreated(null);
//        commentRepository.save(comment1);
//        commentRepository.save(comment2);
//
//        long user1Id = user1.getId();
//        long user2Id = user2.getId();
//
//        ItemRequestDto itemDtoExpectedForUser1 = ItemRequestMapper.toItemRequestDto(itemRequest1);
//        itemDtoExpectedForUser1.setItems(List.of(ItemMapper.toItemDtoCreateUpdate(item1)));
//        ItemRequestDto itemDtoExpectedForUser2 = ItemRequestMapper.toItemRequestDto(itemRequest2);
//        itemDtoExpectedForUser2.setItems(List.of(ItemMapper.toItemDtoCreateUpdate(item2)));
//
//        List<ItemRequestDto> itemRequestDtosForUser1 = service.findItemRequests(user1Id);
//        assertThat(itemRequestDtosForUser1, equalTo(List.of(itemDtoExpectedForUser1)));
//        List<ItemRequestDto> itemRequestDtosForUser2 = service.findItemRequests(user2Id);
//        assertThat(itemRequestDtosForUser2, equalTo(List.of(itemDtoExpectedForUser2)));
//    }

}