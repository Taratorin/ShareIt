package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest
class UserControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

//    @SneakyThrows
//    @Test
//    void createUser() {
//        UserDto userDto = getUserDtoList().get(0);
//        when(userService.saveUser(userDto)).thenReturn(userDto);
//        String result = mockMvc.perform(post("/user")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        assertThat(objectMapper.writeValueAsString(userDto), equals(result));
//
//    }

//    @SneakyThrows
//    @Test
//    void updateUser_whenUserIsNotValid_thenBadRequest() {
//        Long userId = 0L;
//        UserDto userDto = getUserDtoList().get(0);
//        userDto.setName(null);
//        mockMvc.perform(put("/user/{userId}", userId)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isBadRequest());
//        verify(userService, never()).updateUser(userDto, userId);
//
//    }

//    @Test
//    void getAllUsers() {
//    }

//    @SneakyThrows
//    @Test
//    void getUser() {
//        long userId = 0L;
//        mockMvc.perform(get("/user/{id}", userId))
//                .andDo(print())
//                .andExpect(status().isOk());
//        verify(userService).findUserDtoById(userId);
//    }

//    @Test
//    void deleteUser() {
//    }

    private List<UserDto> getUserDtoList() {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(
                UserDto.builder()
                        .id(1)
                        .name("Пользователь 1")
                        .email("email1@email.com")
                        .build()
        );
        userDtos.add(
                UserDto.builder()
                        .id(2)
                        .name("Пользователь 2")
                        .email("email2@email.com")
                        .build()
        );
        userDtos.add(
                UserDto.builder()
                        .id(3)
                        .name("Пользователь 3")
                        .email("email3@email.com")
                        .build()
        );
        return userDtos;
    }
}