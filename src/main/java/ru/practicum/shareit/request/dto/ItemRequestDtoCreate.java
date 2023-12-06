package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDtoCreate {
    private long id;
    @NotBlank
    @Size(max = 512)
    private String description;
}
