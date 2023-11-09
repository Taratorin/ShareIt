package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserDao userDao;
    private final ItemDao itemDao;
    private int id;

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        if (isItemDtoValid(itemDto)) {
            User user = userDao.getUserById(userId);
            if (user != null) {
                Item item = ItemMapper.toItem(itemDto);
                item.setId(getId());
                item.setOwner(user);
                Item itemFromDao = itemDao.createItem(item);
                return ItemMapper.toItemDto(itemFromDao);
            } else {
                throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
            }
        } else {
            throw new BadRequestException("Вещь должна содержать имя, описание и сведения о доступности.");
        }
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        Item itemToUpdate = itemDao.getItemById(itemId);
        if (itemToUpdate != null) {
            if (itemToUpdate.getOwner().getId() == userId) {
                if (itemDto.getName() != null) {
                    itemToUpdate.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    itemToUpdate.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    itemToUpdate.setAvailable(itemDto.getAvailable());
                }
                Item itemFromDao = itemDao.updateItem(itemToUpdate);
                return ItemMapper.toItemDto(itemFromDao);
            } else {
                throw new ForbiddenException("Запрещено изменять вещи другого пользователя.");
            }
        } else {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена.");
        }
    }

    @Override
    public ItemDto getItemById(int itemId) {
        Item item = itemDao.getItemById(itemId);
        if (item != null) {
            return ItemMapper.toItemDto(item);
        } else {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена.");
        }
    }

    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        User user = userDao.getUserById(userId);
        if (user != null) {
            List<Item> items = itemDao.getItemsByUserId(userId);
            return items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }

    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (!text.isBlank()) {
            String query = text.toLowerCase();
            List<Item> items = itemDao.searchItem(query);
            return items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private int getId() {
        return ++id;
    }

    private Boolean isItemDtoValid(ItemDto itemDto) {
        return (itemDto.getName() != null &&
                !itemDto.getName().isBlank() &&
                itemDto.getDescription() != null &&
                !itemDto.getDescription().isBlank() &&
                itemDto.getAvailable() != null);
    }
}
