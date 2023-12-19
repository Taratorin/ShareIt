package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.config.Constants.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping()
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDtoCreate itemRequestDtoCreate,
                                                    @RequestHeader(X_SHARER_USER_ID) @Min(1) long requestorId) {
        log.info("Получен запрос POST /requests — добавление запроса на вещь");
        return itemRequestClient.saveRequest(itemRequestDtoCreate, requestorId);
    }

    @GetMapping()
    public ResponseEntity<Object> findItemRequests(@RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос GET /requests — получение списка запросов вещей текущего пользователя");
        return itemRequestClient.findItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findItemRequests(@RequestParam(defaultValue = "1") @Min(0) int from,
                                                 @RequestParam(defaultValue = "10") @Min(1) int size,
                                                 @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос GET /requests/all — получение списка всех запросов вещей");
        return itemRequestClient.findItemRequestsPages(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@PathVariable @Min(1) long requestId,
                                              @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос GET /requests/{requestId} — получение списка всех запросов вещей");
        return itemRequestClient.findItemRequestById(userId, requestId);
    }
}
