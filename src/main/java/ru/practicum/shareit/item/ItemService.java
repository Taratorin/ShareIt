package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreateUpdate;

import java.util.List;

public interface ItemService {
    ItemDtoCreateUpdate saveItem(ItemDtoCreateUpdate itemDtoCreateUpdate, long userId);

    ItemDtoCreateUpdate updateItem(ItemDtoCreateUpdate itemDtoCreateUpdate, long itemId, long userId);

    ItemDto findItemDtoById(long itemId, long userId);

    List<ItemDto> findItemsByUserId(long userId);

    List<ItemDto> searchItem(String text);

    CommentDto saveComment(CommentDtoCreate commentDtoCreate, long itemId, long userId);
}
