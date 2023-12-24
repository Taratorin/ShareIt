package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void createUser_validRequest() {
        UserDto userDto = getUserDtos().get(0);
        when(userService.saveUser(userDto)).thenReturn(userDto);
        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(objectMapper.writeValueAsString(userDto), equalTo(result));
        verify(userService).saveUser(userDto);
    }

    @SneakyThrows
    @Test
    void createUser_whenAnyExceptionThrown_thenBadRequest() {
        UserDto userDto = getUserDtos().get(0);
        when(userService.saveUser(userDto)).thenThrow(new RuntimeException());
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
        verify(userService, times(1)).saveUser(userDto);
    }

    @SneakyThrows
    @Test
    void createUser_whenAnyNotFoundExceptionThrown() {
        UserDto userDto = getUserDtos().get(0);
        when(userService.saveUser(userDto)).thenThrow(new NotFoundException(""));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).saveUser(userDto);
    }

    @SneakyThrows
    @Test
    void findAllUsers() {
        List<UserDto> userDtos = getUserDtos();
        when(userService.findAllUsers()).thenReturn(userDtos);
        String result = mockMvc.perform(get("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtos)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(result, equalTo(objectMapper.writeValueAsString(userDtos)));
        verify(userService).findAllUsers();
    }

    private List<UserDto> getUserDtos() {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(
                UserDto.builder()
                        .id(1)
                        .name("User 1")
                        .email("email1@email.com")
                        .build()
        );
        userDtos.add(
                UserDto.builder()
                        .id(2)
                        .name("User 2")
                        .email("email2@email.com")
                        .build()
        );
        userDtos.add(
                UserDto.builder()
                        .id(3)
                        .name("User 3")
                        .email("email3@email.com")
                        .build()
        );
        return userDtos;
    }
}