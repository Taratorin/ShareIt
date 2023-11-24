package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, long userId) {
            Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        item.setOwner(user);
            return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = getItemById(itemId);
        if (item.getOwner().getId() == userId) {
            String name = itemDto.getName();
            String description = itemDto.getDescription();
            Boolean available = itemDto.getAvailable();
            if (name != null && !name.isBlank()) {
                item.setName(name);
            }
            if (description != null && !description.isBlank()) {
                item.setDescription(description);
            }
            if (available != null) {
                item.setIsAvailable(available);
            }
            return ItemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new ForbiddenException("Запрещено изменять вещи другого пользователя.");
        }
    }

    @Override
    public ItemDto findItemDtoById(long itemId) {
        Item item = getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findItemsByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (!text.isBlank()) {
            List<Item> items = itemRepository.findAllByDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(text);
            return items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    private Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));
    }
}
