package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreateUpdate;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
        User user = User.builder()
                .id(1)
                .name("Имя пользователя")
                .email("email@email.com")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Item itemToSave = ItemMapper.toItem(itemDtoCreateUpdate);
        itemToSave.setOwner(user);
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
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(getItem()));
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
                () -> service.updateItem(itemDtoCreateUpdate,1, 10));
    }

    @Test
    void findItemDtoById_whenItemFound_thenReturnItemDto() {
        ItemDto itemDto = getItemDto();
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(getItem()));
        Mockito.when(commentRepository.findAllByItem(any()))
                .thenReturn(List.of());
        ItemDto itemDtoById = service.findItemDtoById(1, 1);
        assertThat(itemDtoById, equalTo(itemDto));
    }

    @Test
    void findItemDtoById_whenItemNotFound_thenNotFoundException() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.findItemDtoById(1, 1));
    }

    @Test
    void findItemsByUserId() {
    }

    @Test
    void searchItem() {
    }

    @Test
    void saveComment() {
    }

    private ItemDtoCreateUpdate getItemDtoCreateUpdate() {
        return ItemDtoCreateUpdate.builder()
                .name("Моя новая вещь")
                .description("Описание моей новой вещи")
                .available(true)
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
}