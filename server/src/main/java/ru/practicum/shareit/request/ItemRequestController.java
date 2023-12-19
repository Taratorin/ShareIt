package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;

import java.util.List;

import static ru.practicum.shareit.config.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDtoCreate itemRequestDtoCreate,
                                        @RequestHeader(X_SHARER_USER_ID) long requestorId) {
        log.info("Получен запрос POST /requests — добавление запроса на вещь");
        return itemRequestService.saveRequest(itemRequestDtoCreate, requestorId);
    }

    @GetMapping()
    public List<ItemRequestDto> findItemRequests(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос GET /requests — получение списка запросов вещей текущего пользователя");
        return itemRequestService.findItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findItemRequests(@RequestParam(defaultValue = "1") int from,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос GET /requests/all — получение списка всех запросов вещей");
        return itemRequestService.findItemRequestsPages(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestById(@PathVariable long requestId,
                                              @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получен запрос GET /requests/{requestId} — получение списка всех запросов вещей");
        return itemRequestService.findItemRequestById(userId, requestId);
    }

}
