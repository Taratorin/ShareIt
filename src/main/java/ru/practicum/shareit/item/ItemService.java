package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    ItemDto getItemById(int itemId);

    List<ItemDto> getItemsByUserId(int userId);

    List<ItemDto> searchItem(String text);
}
