package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDtoCreate itemRequestDtoCreate, User requestor) {
        return ItemRequest.builder()
                .description(itemRequestDtoCreate.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .items(List.of())
                .created(itemRequest.getCreated())
                .build();
    }

}
