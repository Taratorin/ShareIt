package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(long ownerId);

    List<Item> findAllByIsAvailableIsTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(String textDescription, String textName);

    List<Item> findAllByRequestInOrderById(List<ItemRequest> requests);

}
