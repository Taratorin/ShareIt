package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserDao userDao;
    private final ItemDao itemDao;
    private int id;

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
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
    }

    private int getId() {
        return ++id;
    }
}
