package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ResponseRepository responseRepository;

    @Override
    public ItemRequestDto saveRequest(ItemRequestDtoCreate itemRequestDtoCreate, long requestorId) {
        User booker = findUserById(requestorId);
        ItemRequest itemRequest = requestRepository
                .save(ItemRequestMapper.toItemRequest(itemRequestDtoCreate, booker));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findItemRequests(long userId) {
        User user = findUserById(userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorOrderByCreatedDesc(user);
        Map<ItemRequest, List<Response>> responses = responseRepository.findAllByRequestInOrderByCreatedDesc(itemRequests).stream()
                .collect(Collectors.groupingBy(Response::getRequest));
        return itemRequests.stream().map(x -> {
                    List<Response> responsesForRequest = responses.getOrDefault(x, List.of());
                    return ItemRequestMapper.toItemRequestDto(x, responsesForRequest);
                })
                .collect(Collectors.toList());
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
    }
}
