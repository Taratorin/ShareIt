package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemDaoInMemory implements ItemDao {

    private final Map<Integer, Item> items;
    private int id;


    @Override
    public Item createItem(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Boolean isItemByIdExists(int id) {
        return items.containsKey(id);
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(int id) {
        return Optional.of(items.get(id));
    }

    @Override
    public List<Item> getItemsByUserId(int userId) {
        return items.values().stream()
                .filter(x -> x.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String query) {
        return items.values().stream()
                .filter(x -> (x.getAvailable() &&
                        (x.getName().toLowerCase().contains(query) ||
                        x.getDescription().toLowerCase().contains(query))))
                .collect(Collectors.toList());
    }

    private int getId() {
        return ++id;
    }

}