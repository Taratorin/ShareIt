package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl service;


    @Test

    void saveRequest() {
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequestDtoCreate itemRequestDtoCreate = getItemRequestDtoCreate();
        when(itemRequestRepository.save(any())).thenReturn(getItemRequest());
        ItemRequestDto itemRequestDto = service.saveRequest(itemRequestDtoCreate, 1);

        assertThat(itemRequestDto, equalTo(getItemRequestDto()));
        verify(itemRequestRepository).save(any());
    }

    @Test
    void findItemRequests() {
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(any())).thenReturn(List.of(getItemRequest()));
        when(itemRepository.findAllByRequestInOrderById(any())).thenReturn(List.of());

        List<ItemRequestDto> itemRequestDtos = service.findItemRequests(1L);
        assertThat(itemRequestDtos, equalTo(List.of(getItemRequestDto())));
    }

    @Test
    void findItemRequestsPages() {
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(any(), any())).thenReturn(List.of(getItemRequest()));
        when(itemRepository.findAllByRequestInOrderById(any())).thenReturn(List.of());

        List<ItemRequestDto> itemRequestDtos = service.findItemRequestsPages(1L, 1, 10);
        assertThat(itemRequestDtos, equalTo(List.of(getItemRequestDto())));
    }

    @Test
    void findItemRequestById() {
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(getItemRequest()));
        when(itemRepository.findAllByRequestInOrderById(any())).thenReturn(List.of());

        ItemRequestDto itemRequestDto = service.findItemRequestById(1L, 1L);
        assertThat(itemRequestDto, equalTo(getItemRequestDto()));
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

    private ItemRequest getItemRequest() {
        return ItemRequest.builder()
                .id(1)
                .description("Description for Item Request")
                .requestor(getUsers().get(0))
                .created(LocalDateTime.of(2023, 12, 1, 12, 0))
                .build();
    }

    private ItemRequestDtoCreate getItemRequestDtoCreate() {
        ItemRequest itemRequest = getItemRequest();
        return ItemRequestDtoCreate.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .build();
    }

    private ItemRequestDto getItemRequestDto() {
        ItemRequest itemRequest = getItemRequest();
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }
}