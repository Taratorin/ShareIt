package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserDao userDao;
    private final ItemDao itemDao;
    private final ModelMapper mapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        if (userDao.isUserByIdExists(userId)) {
            Item item = mapper.map(itemDto, Item.class);
            User user = userDao.getUserById(userId)
                    .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
            item.setOwner(user);
            Item itemFromDao = itemDao.createItem(item);
            return mapper.map(itemFromDao, ItemDto.class);
        } else {
            throw new NotFoundException("Пользователь с id=" + userId + " не существует.");
        }
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        Item itemToUpdate = getItemById(itemId);
        if (itemToUpdate.getOwner().getId() == userId) {
            String name = itemDto.getName();
            String description = itemDto.getDescription();
            Boolean available = itemDto.getAvailable();
            if (name != null && !name.isBlank()) {
                itemToUpdate.setName(name);
            }
            if (description != null && !description.isBlank()) {
                itemToUpdate.setDescription(description);
            }
            if (available != null) {
                itemToUpdate.setAvailable(available);
            }
            return mapper.map(itemToUpdate, ItemDto.class);
        } else {
            throw new ForbiddenException("Запрещено изменять вещи другого пользователя.");
        }
    }

    @Override
    public ItemDto getItemDtoById(int itemId) {
        Item item = getItemById(itemId);
        return mapper.map(item, ItemDto.class);
    }

    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        userDao.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        List<Item> items = itemDao.getItemsByUserId(userId);
        return items.stream()
                .map(x -> mapper.map(x, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (!text.isBlank()) {
            String query = text.toLowerCase();
            List<Item> items = itemDao.searchItem(query);
            return items.stream()
                    .map(x -> mapper.map(x, ItemDto.class))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    private Item getItemById(int itemId) {
        return itemDao.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));
    }
}
