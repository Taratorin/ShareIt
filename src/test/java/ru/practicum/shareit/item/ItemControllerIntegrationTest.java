package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreateUpdate;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.config.Constants.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void saveItem_whenItemNameNotValid_thenBadRequest() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        itemDtoCreateUpdate.setName(" ");
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).saveItem(itemDtoCreateUpdate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void saveItem_whenItemDescriptionNotValid_thenBadRequest() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        itemDtoCreateUpdate.setDescription(" ");
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).saveItem(itemDtoCreateUpdate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void saveItem_whenItemAvailableNotValid_thenBadRequest() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        itemDtoCreateUpdate.setAvailable(null);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).saveItem(itemDtoCreateUpdate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void saveItem_whenUserIdNotValid_thenInternalServerError() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .header(X_SHARER_USER_ID, -1L)
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).saveItem(itemDtoCreateUpdate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void saveItem_whenExceptionThrown() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        when(itemService.saveItem(itemDtoCreateUpdate, 1L)).thenThrow(new NotFoundException(""));
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
        verify(itemService, times(1)).saveItem(itemDtoCreateUpdate, 1L);
    }

    @SneakyThrows
    @Test
    void saveItem_whenAllIsValid() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        when(itemService.saveItem(itemDtoCreateUpdate, 1L)).thenReturn(itemDtoCreateUpdate);
        String result = mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).saveItem(itemDtoCreateUpdate, 1L);
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemDtoCreateUpdate)));
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemIdIsNotValid() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        long itemId = -1;
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).updateItem(any(ItemDtoCreateUpdate.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem_whenUserIdIsNotValid() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        long itemId = 1;
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .header(X_SHARER_USER_ID, -1L)
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).updateItem(any(ItemDtoCreateUpdate.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem_whenForbiddenExceptionThrown() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        when(itemService.updateItem(any(ItemDtoCreateUpdate.class), anyLong(), anyLong()))
                .thenThrow(new ForbiddenException(""));
        long itemId = 1;
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType("application/json"))
                .andExpect(status().isForbidden());
        verify(itemService, times(1)).updateItem(itemDtoCreateUpdate, 1L, 1L);
    }

    @SneakyThrows
    @Test
    void updateItem_allIsValid() {
        ItemDtoCreateUpdate itemDtoCreateUpdate = getItemDtoCreateUpdate();
        when(itemService.updateItem(any(ItemDtoCreateUpdate.class), anyLong(), anyLong()))
                .thenReturn(itemDtoCreateUpdate);
        long itemId = 1;
        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(itemDtoCreateUpdate))
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService, times(1)).updateItem(itemDtoCreateUpdate, 1L, 1L);
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemDtoCreateUpdate)));
    }

    @SneakyThrows
    @Test
    void saveComment_whenCommentIsBlank_thenBadRequest() {
        CommentDtoCreate commentDtoCreate = getCommentDtoCreate();
        commentDtoCreate.setText(" ");
        long itemId = 1L;
        long userId = 1L;
        when(itemService.saveComment(commentDtoCreate, itemId, userId)).thenReturn(getCommentDto());
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDtoCreate))
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).saveComment(commentDtoCreate, itemId, userId);
    }

    @SneakyThrows
    @Test
    void saveComment_whenItemIdNotValid() {
        CommentDtoCreate commentDtoCreate = getCommentDtoCreate();
        long itemId = -1L;
        long userId = 1L;
        when(itemService.saveComment(commentDtoCreate, itemId, userId)).thenReturn(getCommentDto());
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDtoCreate))
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).saveComment(commentDtoCreate, itemId, userId);
    }

    @SneakyThrows
    @Test
    void saveComment_whenUserIdNotValid() {
        CommentDtoCreate commentDtoCreate = getCommentDtoCreate();
        long itemId = 1L;
        long userId = -1L;
        when(itemService.saveComment(commentDtoCreate, itemId, userId)).thenReturn(getCommentDto());
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDtoCreate))
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).saveComment(commentDtoCreate, itemId, userId);
    }

    @SneakyThrows
    @Test
    void saveComment_whenAllIsValid() {
        CommentDtoCreate commentDtoCreate = getCommentDtoCreate();
        long itemId = 1L;
        long userId = 1L;
        when(itemService.saveComment(commentDtoCreate, itemId, userId)).thenReturn(getCommentDto());
        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDtoCreate))
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).saveComment(commentDtoCreate, itemId, userId);
        assertThat(result, equalTo(objectMapper.writeValueAsString(getCommentDto())));
    }

    @SneakyThrows
    @Test
    void getItem_whenItemIsIsNotValid() {
        ItemDto itemDto = getItemDto();
        long itemId = -1L;
        long userId = 1L;
        when(itemService.findItemDtoById(itemId, userId)).thenReturn(itemDto);
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(X_SHARER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).findItemDtoById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItem_whenUserIsIsNotValid() {
        ItemDto itemDto = getItemDto();
        long itemId = 1L;
        long userId = -1L;
        when(itemService.findItemDtoById(itemId, userId)).thenReturn(itemDto);
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(X_SHARER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).findItemDtoById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItem_whenAllIsValid() {
        ItemDto itemDto = getItemDto();
        long itemId = 1L;
        long userId = 1L;
        when(itemService.findItemDtoById(itemId, userId)).thenReturn(itemDto);
        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(X_SHARER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemDto)));
        verify(itemService, times(1)).findItemDtoById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItemByUserId_whenFromIsNotValid() {
        List<ItemDto> itemDtos = List.of(getItemDto());
        long userId = 1L;
        int from = -1;
        int size = 10;
        mockMvc.perform(get("/items")
                        .contentType("application/json")
                        .header(X_SHARER_USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(objectMapper.writeValueAsString(itemDtos)))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).findItemsByUserId(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getItemByUserId_whenSizeIsNotValid() {
        List<ItemDto> itemDtos = List.of(getItemDto());
        long userId = 1L;
        int from = 1;
        int size = -10;
        mockMvc.perform(get("/items")
                        .contentType("application/json")
                        .header(X_SHARER_USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(objectMapper.writeValueAsString(itemDtos)))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).findItemsByUserId(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getItemByUserId_whenAllIsValid() {
        List<ItemDto> itemDtos = List.of(getItemDto());
        long userId = 1L;
        int from = 0;
        int size = 10;
        when(itemService.findItemsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(itemDtos);
        String result = mockMvc.perform(get("/items")
                        .contentType("application/json")
                        .header(X_SHARER_USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(objectMapper.writeValueAsString(itemDtos)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemDtos)));
        verify(itemService, times(1)).findItemsByUserId(userId, from, size);
    }

    @SneakyThrows
    @Test
    void searchItem() {
        List<ItemDto> itemDtos = List.of(getItemDto());
        String text = "request";
        int from = 0;
        int size = 10;
        when(itemService.searchItem(text, from, size)).thenReturn(itemDtos);
        String result = mockMvc.perform(get("/items/search")
                        .contentType("application/json")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(objectMapper.writeValueAsString(itemDtos)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(result, equalTo(objectMapper.writeValueAsString(itemDtos)));
        verify(itemService, times(1)).searchItem(text, from, size);
    }

    private ItemDtoCreateUpdate getItemDtoCreateUpdate() {
        return ItemDtoCreateUpdate.builder()
                .name("My new thing")
                .description("Description of my new thing")
                .available(true)
                .requestId(1L)
                .build();
    }

    private CommentDtoCreate getCommentDtoCreate() {
        return CommentMapper.toCommentDtoCreate(getComment());
    }

    private CommentDto getCommentDto() {
        return CommentMapper.toCommentDto(getComment());
    }

    private Comment getComment() {
        return Comment.builder()
                .id(1)
                .text("Some comment")
                .item(getItemForTest())
                .author(getUsers().get(0))
                .created(LocalDateTime.of(2023, 10, 10, 12, 0))
                .build();
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

    private Item getItemForTest() {
        return Item.builder()
                .name("My new thing")
                .description("Description of my new thing")
                .isAvailable(true)
                .owner(getUsers().get(0))
                .build();
    }

    private ItemDto getItemDto() {
        return ItemMapper.toItemDto(getItemForTest(), List.of());
    }
}