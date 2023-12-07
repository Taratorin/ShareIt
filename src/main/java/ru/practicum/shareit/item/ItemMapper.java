package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDtoCreateUpdate toItemDtoCreateUpdate(Item item) {
        ItemDtoCreateUpdate itemDtoCreateUpdate = ItemDtoCreateUpdate.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
        ItemRequest itemRequest = item.getRequest();
        if (itemRequest != null) {
            Long requestId = itemRequest.getId();
            itemDtoCreateUpdate.setRequestId(requestId);
        }
        return itemDtoCreateUpdate;
    }

    public ItemBookingDto toItemBookingDto(Item item) {
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public ItemDto toItemDto(Item item, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .comments(comments)
                .build();
    }

    public ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        BookingForDto lastBookingForDto = null;
        BookingForDto nextBookingForDto = null;
        if (lastBooking != null) {
            lastBookingForDto = new BookingForDto(lastBooking.getId(),
                    lastBooking.getStart(), lastBooking.getEnd(), ItemMapper.toItemBookingDto(lastBooking.getItem()),
                    lastBooking.getBooker().getId(), lastBooking.getStatus());
        }
        if (nextBooking != null) {
            nextBookingForDto = new BookingForDto(nextBooking.getId(), nextBooking.getStart(),
                    nextBooking.getEnd(), ItemMapper.toItemBookingDto(nextBooking.getItem()),
                    nextBooking.getBooker().getId(), nextBooking.getStatus());
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .lastBooking(lastBookingForDto)
                .nextBooking(nextBookingForDto)
                .comments(comments)
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .build();
    }

    public Item toItem(ItemDtoCreateUpdate itemDtoCreateUpdate) {
        return Item.builder()
                .name(itemDtoCreateUpdate.getName())
                .description(itemDtoCreateUpdate.getDescription())
                .isAvailable(itemDtoCreateUpdate.getAvailable())
                .build();
    }
}
