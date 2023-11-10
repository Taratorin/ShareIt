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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserDao userDao;
    private final ItemDao itemDao;

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        if (isItemDtoValid(itemDto)) {
            User user = userDao.getUserById(userId);
            if (user != null) {
                Item item = ItemMapper.toItem(itemDto);
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
        Item itemToUpdate = getItemById(itemId);
        if (itemToUpdate != null) {
            if (itemToUpdate.getOwner().getId() == userId) {
                if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                    itemToUpdate.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
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
    public ItemDto getItemDtoById(int itemId) {
        Item item = getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        userDao.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден."));
        List<Item> items = itemDao.getItemsByUserId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
            return List.of();
        }
    }

    //todo удалить метод
//    private Boolean isItemDtoValid(ItemDto itemDto) {
//        return (itemDto.getName() != null &&
//                !itemDto.getName().isBlank() &&
//                itemDto.getDescription() != null &&
//                !itemDto.getDescription().isBlank() &&
//                itemDto.getAvailable() != null);
//    }

    private Item getItemById(int itemId) {
        return itemDao.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));
    }
}
