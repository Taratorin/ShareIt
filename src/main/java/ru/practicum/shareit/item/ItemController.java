package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.config.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto saveItem(@Valid @RequestBody ItemDto itemDto,
                            @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос POST /items — добавление вещи");
        return itemService.saveItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@Valid @RequestBody CommentDto commentDto,
                               @PathVariable @Min(1) long itemId,
                               @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос POST /items/{itemId}/comment — добавление комментария к вещи");
        return itemService.saveComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable @Min(1) long itemId,
                              @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос PATCH /items — обновление вещи");
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId,
                           @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос GET /items/{itemId} — получение вещи по id");
        return itemService.findItemDtoById(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItemByUserId(@RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос GET /items — получение вещи по id пользователя");
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("Получен запрос GET /items/search — поиск вещи по значению строки");
        return itemService.searchItem(text);
    }

}
