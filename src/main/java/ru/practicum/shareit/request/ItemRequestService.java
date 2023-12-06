package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveRequest(ItemRequestDtoCreate itemRequestDtoCreate, long requestorId);

    List<ItemRequestDto> findItemRequests(long userId);
}
