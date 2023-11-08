package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemDaoInMemory implements ItemDao {

    private final Map<Integer, Item> items;

    @Override
    public Item createItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }
}
