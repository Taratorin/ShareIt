package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoCreateUpdate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto saveRequest(ItemRequestDtoCreate itemRequestDtoCreate, long requestorId) {
        User requestor = findUserById(requestorId);
        ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDtoCreate, requestor);
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> findItemRequests(long userId) {
        User user = findUserById(userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorOrderByCreatedDesc(user);
        Map<ItemRequest, List<Item>> items = itemRepository.findAllByRequestInOrderById(itemRequests).stream()
                .collect(Collectors.groupingBy(Item::getRequest));
        return itemRequests.stream().map(x -> {
                    List<ItemDtoCreateUpdate> itemDtoCreateUpdates = items.getOrDefault(x, List.of()).stream()
                            .map(ItemMapper::toItemDtoCreateUpdate)
                            .collect(Collectors.toList());
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(x);
                    itemRequestDto.setItems(itemDtoCreateUpdates);
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findItemRequestsPages(long userId, int from, int size) {
        User user = findUserById(userId);
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<ItemRequest> itemRequests = new ArrayList<>(requestRepository.findAllByRequestorNotOrderByCreatedDesc(user, pageable));
        Map<ItemRequest, List<Item>> items = itemRepository.findAllByRequestInOrderById(itemRequests).stream()
                .collect(Collectors.groupingBy(Item::getRequest));
        return itemRequests.stream().map(x -> {
                    List<ItemDtoCreateUpdate> itemDtoCreateUpdates = items.getOrDefault(x, List.of()).stream()
                            .map(ItemMapper::toItemDtoCreateUpdate)
                            .collect(Collectors.toList());
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(x);
                    itemRequestDto.setItems(itemDtoCreateUpdates);
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findItemRequestById(long userId, long requestId) {
        findUserById(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не существует."));
        List<ItemDtoCreateUpdate> itemDtoCreateUpdates = itemRepository.findAllByRequestInOrderById(List.of(itemRequest)).stream()
                .map(ItemMapper::toItemDtoCreateUpdate)
                .collect(Collectors.toList());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemDtoCreateUpdates);
        return itemRequestDto;
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
    }
}
