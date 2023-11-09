package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") @Min(1) int userId) {
        log.info("Получен запрос POST /items — добавление вещи");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto,
                              @PathVariable int itemId,
                              @RequestHeader("X-Sharer-User-Id") @Min(1) int userId) {
        log.info("Получен запрос PATCH /items — обновление вещи");
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        log.info("Получен запрос GET /items/{itemId} — получение вещи по id");
        return itemService.getItemById(itemId);
    }

    @GetMapping()
    public List<ItemDto> getItemByUserId(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId) {
        log.info("Получен запрос GET /items — получение вещи по id пользователя");
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("Получен запрос GET /items/search — поиск вещи по значению строки");
        return itemService.searchItem(text);
    }

}
