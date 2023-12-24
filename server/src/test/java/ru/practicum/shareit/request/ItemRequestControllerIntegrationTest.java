package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.config.Constants.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void createItemRequest_whenAllIsValid() {
        ItemRequestDtoCreate itemRequestDtoCreate = getItemRequestDtoCreate();
        ItemRequestDto itemRequestDto = getItemRequestDto();
        long requestorId = 1L;
        when(itemRequestService.saveRequest(itemRequestDtoCreate, requestorId)).thenReturn(itemRequestDto);
        String result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDtoCreate))
                        .header(X_SHARER_USER_ID, requestorId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService, times(1)).saveRequest(itemRequestDtoCreate, requestorId);
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @SneakyThrows
    @Test
    void findItemRequests() {
        List<ItemRequestDto> itemRequestDtos = List.of(getItemRequestDto());
        long userId = 1L;
        when(itemRequestService.findItemRequests(userId)).thenReturn(itemRequestDtos);
        String result = mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService, times(1)).findItemRequests(userId);
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemRequestDtos)));
    }

    @SneakyThrows
    @Test
    void testFindItemRequests() {
        List<ItemRequestDto> itemRequestDtos = List.of(getItemRequestDto());
        long userId = 1L;
        int from = 1;
        int size = 10;
        when(itemRequestService.findItemRequestsPages(userId, from, size)).thenReturn(itemRequestDtos);
        String result = mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService, times(1)).findItemRequestsPages(userId, from, size);
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemRequestDtos)));
    }

    @SneakyThrows
    @Test
    void findItemRequestById() {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        long userId = 1L;
        long requestId = 1L;
        when(itemRequestService.findItemRequestById(userId, requestId)).thenReturn(itemRequestDto);
        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService, times(1)).findItemRequestById(userId, requestId);
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemRequestDto)));
    }

    private ItemRequest getItemRequest() {
        return ItemRequest.builder()
                .id(1)
                .description("Description for Item Request")
                .requestor(getUsers().get(0))
                .created(LocalDateTime.of(2023, 12, 1, 12, 0))
                .build();
    }

    private ItemRequestDtoCreate getItemRequestDtoCreate() {
        ItemRequest itemRequest = getItemRequest();
        return ItemRequestDtoCreate.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .build();
    }

    private ItemRequestDto getItemRequestDto() {
        ItemRequest itemRequest = getItemRequest();
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private List<User> getUsers() {
        return new ArrayList<>(
                List.of(
                        User.builder()
                                .id(1)
                                .name("Name of User 1")
                                .email("email1@email.com")
                                .build(),
                        User.builder()
                                .id(2)
                                .name("Name of User 2")
                                .email("email2@email.com")
                                .build()
                )
        );
    }
}