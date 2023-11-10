package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.config.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private int id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email
    @NotEmpty(groups = {Create.class})
    private String email;
}
