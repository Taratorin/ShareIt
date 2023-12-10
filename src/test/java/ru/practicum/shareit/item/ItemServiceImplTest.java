package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreateUpdate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl service;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    void saveItem_whenItemValid_thenSavedItem() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest request = getItemRequests().get(0);
        when(requestRepository.findById(any())).thenReturn(Optional.ofNullable(request));
        Item itemToSave = ItemMapper.toItem(itemDtoCreateUpdate);
        itemToSave.setOwner(user);
        itemToSave.setRequest(request);
        when(itemRepository.save(any())).thenReturn(itemToSave);
        long userId = 1L;
        ItemDtoCreateUpdate itemDtoCreateUpdateSaved = service.saveItem(itemDtoCreateUpdate, userId);

        assertThat(itemDtoCreateUpdateSaved, equalTo(itemDtoCreateUpdate));
        verify(itemRepository).save(any());
    }

    @Test
    void updateItem_whenIdValid_thenItemUpdated() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        itemDtoCreateUpdate.setName("Имя после обновления");
        itemDtoCreateUpdate.setDescription("Описание после обновления");
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getItem()));
        when(itemRepository.save(any())).thenReturn(getItem());
        service.updateItem(itemDtoCreateUpdate, 1, 1);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item itemToSave = itemArgumentCaptor.getValue();
        assertThat(itemToSave.getName(), equalTo(itemDtoCreateUpdate.getName()));
        assertThat(itemToSave.getDescription(), equalTo(itemDtoCreateUpdate.getDescription()));
        assertThat(itemToSave.getIsAvailable(), equalTo(itemDtoCreateUpdate.getAvailable()));
    }

    @Test
    void updateItem_whenIdNotValid_thenForbiddenException() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        itemDtoCreateUpdate.setName("Имя после обновления");
        itemDtoCreateUpdate.setDescription("Описание после обновления");
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(getItem()));
        assertThrows(ForbiddenException.class,
                () -> service.updateItem(itemDtoCreateUpdate, 1, 10));
    }

    @Test
    void findItemDtoById_whenItemFound_thenReturnItemDto() {
        ItemDto itemDto = getItemDto();
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(getItem()));
        when(commentRepository.findAllByItem(any())).thenReturn(List.of());
        ItemDto itemDtoById = service.findItemDtoById(1, 1);
        assertThat(itemDtoById, equalTo(itemDto));
    }

    @Test
    void findItemDtoById_whenItemFoundAndOwnerNotUser_thenReturnItemDto() {
        ItemDto itemDto = getItemDto();
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(getItem()));
        when(commentRepository.findAllByItem(any())).thenReturn(List.of());
        ItemDto itemDtoById = service.findItemDtoById(1, 10);
        assertThat(itemDtoById, equalTo(itemDto));
    }

    @Test
    void findItemDtoById_whenItemNotFound_thenNotFoundException() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.findItemDtoById(1, 1));
    }

    @Test
    void findItemsByUserId_whenItemsFound() {
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        assert user != null;
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any())).thenReturn(List.of(getItem()));
        when(bookingRepository.findAllByItemInAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of());
        when(commentRepository.findAllByItemIn(any())).thenReturn(List.of());
        List<ItemDto> itemDtos = service.findItemsByUserId(1, 1, 10);

        assertThat(itemDtos, equalTo(List.of(ItemMapper.toItemDto(getItem(), List.of()))));
    }

    @Test
    void findItemsByUserId_whenItemsNotFound() {
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        assert user != null;
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any())).thenReturn(List.of());
        when(bookingRepository.findAllByItemInAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of());
        when(commentRepository.findAllByItemIn(any())).thenReturn(List.of());
        List<ItemDto> itemDtos = service.findItemsByUserId(10, 1, 10);

        assertThat(itemDtos, equalTo(List.of()));
    }

    @Test
    void searchItem_whenTextBlank_returnedEmptyList() {
        List<ItemDto> itemDtos = service.searchItem("   ", 1, 10);
        assertThat(itemDtos, equalTo(List.of()));
    }

    @Test
    void searchItem_whenTextNotBlank_returnedList() {
        when(itemRepository.findAllByIsAvailableIsTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(any(), any(), any()))
                .thenReturn(List.of(getItem()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(List.of());
        List<ItemDto> itemDtos = service.searchItem("Some text", 1, 10);

        assertThat(itemDtos, equalTo(List.of(ItemMapper.toItemDto(getItem(), List.of()))));
    }

    @Test
    void saveComment_whenNoBookingByUser() {
        when(bookingRepository.existsByItemAndBookerAndStatus(any(), any(), any())).thenReturn(false);
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        assert user != null;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getItem()));
        CommentDtoCreate comment = getCommentDtoCreate();

        assertThrows(BadRequestException.class,
                () -> service.saveComment(comment, 1L, 1L));
    }

    @Test
    void saveComment_whenBookingIsOngoing() {
        when(bookingRepository.existsByItemAndBookerAndStatus(any(), any(), any())).thenReturn(true);
        when(bookingRepository.existsByItemAndBookerAndStatusAndEndLessThanEqual(any(), any(), any(), any())).thenReturn(false);
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        assert user != null;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getItem()));
        CommentDtoCreate comment = getCommentDtoCreate();

        assertThrows(BadRequestException.class,
                () -> service.saveComment(comment, 1L, 1L));
    }

    @Test
    void saveComment_whenAllIsValid() {
        when(bookingRepository.existsByItemAndBookerAndStatus(any(), any(), any())).thenReturn(true);
        when(bookingRepository.existsByItemAndBookerAndStatusAndEndLessThanEqual(any(), any(), any(), any())).thenReturn(true);
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        assert user != null;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getItem()));
        when(commentRepository.save(any())).thenReturn(getComment());

        CommentDto commentDto = service.saveComment(getCommentDtoCreate(), 1L, 1L);
        assertThat(commentDto, equalTo(getCommentDto()));
        verify(commentRepository).save(any());
    }

    private ItemDtoCreateUpdate getItemDtoCreateUpdate() {
        return ItemDtoCreateUpdate.builder()
                .name("Моя новая вещь")
                .description("Описание моей новой вещи")
                .available(true)
                .requestId(1L)
                .build();
    }

    private ItemDto getItemDto() {
        return ItemDto.builder()
                .name("Моя новая вещь")
                .description("Описание моей новой вещи")
                .available(true)
                .comments(List.of())
                .build();
    }

    private Item getItem() {
        User user = User.builder()
                .id(1)
                .name("Пользователь 1")
                .email("email1@email.com")
                .build();
        return Item.builder()
                .name("Моя новая вещь")
                .description("Описание моей новой вещи")
                .isAvailable(true)
                .owner(user)
                .build();
    }

    private List<User> getUsers() {
        return new ArrayList<>(
                List.of(
                        User.builder()
                                .id(1)
                                .name("Имя пользователя 1")
                                .email("email1@email.com")
                                .build(),
                        User.builder()
                                .id(2)
                                .name("Имя пользователя 2")
                                .email("email2@email.com")
                                .build()
                )
        );
    }

    private List<ItemRequest> getItemRequests() {
        return new ArrayList<>(
                List.of(
                        ItemRequest.builder()
                                .id(1)
                                .description("I need a thing!")
                                .requestor(getUsers().get(0))
                                .build(),
                        ItemRequest.builder()
                                .id(0)
                                .description("I need another new thing!")
                                .requestor(getUsers().get(0))
                                .build()
                )
        );
    }

    private CommentDtoCreate getCommentDtoCreate() {
        return CommentMapper.toCommentDtoCreate(getComment());
    }

    private CommentDto getCommentDto() {
        return CommentMapper.toCommentDto(getComment());
    }

    private Comment getComment() {
        return Comment.builder()
                .id(1)
                .text("Some comment")
                .item(getItem())
                .author(getUsers().get(0))
                .created(LocalDateTime.of(2023,10,10,12,0))
                .build();
    }
}