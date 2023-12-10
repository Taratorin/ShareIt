package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForDto lastBooking;
    private BookingForDto nextBooking;
    private List<CommentDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return id == itemDto.id && Objects.equals(name, itemDto.name) && Objects.equals(description, itemDto.description) && Objects.equals(available, itemDto.available) && Objects.equals(lastBooking, itemDto.lastBooking) && Objects.equals(nextBooking, itemDto.nextBooking) && Objects.equals(comments, itemDto.comments);
    }
}
