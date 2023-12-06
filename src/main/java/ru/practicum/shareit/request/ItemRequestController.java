package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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
    public ItemRequestDto createBooking(@Valid @RequestBody ItemRequestDtoCreate itemRequestDtoCreate,
                                        @RequestHeader(X_SHARER_USER_ID) @Min(1) long requestorId) {
        log.info("Получен запрос POST /requests — добавление запроса на вещь");
        return itemRequestService.saveRequest(itemRequestDtoCreate, requestorId);
    }


    @GetMapping()
    public List<ItemRequestDto> findItemRequests(@RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос GET /requests — получение списка запросов вещей текущего пользователя");
        return itemRequestService.findItemRequests(userId);
    }

}
