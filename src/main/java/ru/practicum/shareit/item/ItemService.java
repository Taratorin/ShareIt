package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemDtoById(long itemId);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> searchItem(String text);
}
