//package ru.practicum.shareit.item;
//
//import org.jeasy.random.EasyRandom;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import ru.practicum.shareit.booking.BookingRepository;
//import ru.practicum.shareit.item.dto.CommentDto;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.request.ItemRequest;
//import ru.practicum.shareit.request.ItemRequestRepository;
//import ru.practicum.shareit.user.User;
//import ru.practicum.shareit.user.UserRepository;
//
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.MatcherAssert.assertThat;
//
//
//@SpringBootTest
//class ItemServiceImplIntegrationTest {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private ItemRepository itemRepository;
//    @Autowired
//    private BookingRepository bookingRepository;
//    @Autowired
//    private CommentRepository commentRepository;
//    @Autowired
//    private ItemRequestRepository requestRepository;
//
//    @Autowired
//    private ItemServiceImpl service;
//    private final EasyRandom easyRandom = new EasyRandom();
//
//    @Test
//    @DirtiesContext
//    public void findItemsByUserId() {
//        User user1 = easyRandom.nextObject(User.class);
//        User user2 = easyRandom.nextObject(User.class);
//        user1 = userRepository.save(user1);
//        user2 = userRepository.save(user2);
//
//        ItemRequest itemRequest1 = easyRandom.nextObject(ItemRequest.class);
//        itemRequest1.setRequestor(user1);
//        itemRequest1 = requestRepository.save(itemRequest1);
//        ItemRequest itemRequest2 = easyRandom.nextObject(ItemRequest.class);
//        itemRequest2.setRequestor(user2);
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
//        Item item3 = easyRandom.nextObject(Item.class);
//        item3.setOwner(user2);
//        item3.setRequest(itemRequest2);
//        item3 = itemRepository.save(item3);
//
//        Comment comment1 = easyRandom.nextObject(Comment.class);
//        Comment comment2 = easyRandom.nextObject(Comment.class);
//        comment1.setItem(item1);
//        comment2.setItem(item2);
//        comment1.setAuthor(user1);
//        comment2.setAuthor(user2);
//        comment1.setCreated(null);
//        comment2.setCreated(null);
//        comment1 = commentRepository.save(comment1);
//        comment2 = commentRepository.save(comment2);
//        CommentDto commentDto1 = CommentMapper.toCommentDto(comment1);
//        CommentDto commentDto2 = CommentMapper.toCommentDto(comment2);
//
//        long user1Id = user1.getId();
//        long user2Id = user2.getId();
//
//        List<ItemDto> itemDtoExpectedForUser1 = List.of(ItemMapper.toItemDto(item1, List.of(commentDto1)), ItemMapper.toItemDto(item2, List.of(commentDto2)));
//        List<ItemDto> itemDtoExpectedForUser2 = List.of(ItemMapper.toItemDto(item3, List.of()));
//
//        List<ItemDto> itemDtosForUser1 = service.findItemsByUserId(user1Id, 1, 10);
//        assertThat(itemDtosForUser1, equalTo(itemDtoExpectedForUser1));
//        List<ItemDto> itemDtosForUser2 = service.findItemsByUserId(user2Id, 1, 10);
//        assertThat(itemDtosForUser2, equalTo(itemDtoExpectedForUser2));
//    }
//
//}