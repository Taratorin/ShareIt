package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreateUpdate;

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
    public ItemDtoCreateUpdate saveItem(@Validated(Create.class) @RequestBody ItemDtoCreateUpdate itemDtoCreateUpdate,
                                        @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос POST /items — добавление вещи");
        return itemService.saveItem(itemDtoCreateUpdate, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoCreateUpdate updateItem(@Validated(Update.class) @RequestBody ItemDtoCreateUpdate itemDtoCreateUpdate,
                              @PathVariable @Min(1) long itemId,
                              @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос PATCH /items — обновление вещи");
        return itemService.updateItem(itemDtoCreateUpdate, itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@Valid @RequestBody CommentDtoCreate commentDtoCreate,
                                  @PathVariable @Min(1) long itemId,
                                  @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос POST /items/{itemId}/comment — добавление комментария к вещи");
        return itemService.saveComment(commentDtoCreate, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId,
                           @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос GET /items/{itemId} — получение вещи по id");
        return itemService.findItemDtoById(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItemByUserId(@RequestHeader(X_SHARER_USER_ID) @Min(1) long userId,
                                         @RequestParam(defaultValue = "1") @Min(0) int from,
                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос GET /items — получение вещи по id пользователя");
        return itemService.findItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(defaultValue = "1") @Min(0) int from,
                                    @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос GET /items/search — поиск вещи по значению строки");
        return itemService.searchItem(text, from, size);
    }

}
