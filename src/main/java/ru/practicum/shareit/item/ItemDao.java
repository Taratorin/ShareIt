package ru.practicum.shareit.item;

import java.util.List;

public interface ItemDao {
    Item createItem(Item item);

    Boolean isItemByIdExists(int id);

    Item updateItem(Item item);

    Item getItemById(int id);

    List<Item> getItemsByUserId(int userId);

    List<Item> searchItem(String query);
}
