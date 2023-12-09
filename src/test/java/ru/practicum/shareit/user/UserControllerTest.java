package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUserCollectionInBody() {
        List<UserDto> expectedUsers = getUserDtoList();
        Mockito.when(userService.findAllUsers()).thenReturn(expectedUsers);
        List<UserDto> allUsers = userController.getAllUsers();
        assertThat(allUsers, equalTo(expectedUsers));
    }

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithEmptyUserCollectionInBody() {
        List<UserDto> allUsers = userController.getAllUsers();
        ResponseEntity<List<UserDto>> response = ResponseEntity.ok(allUsers);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertTrue(allUsers.isEmpty());
    }
    @Test
    void getUser() {
    }

    @Test
    void deleteUser() {
    }

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