package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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

}
