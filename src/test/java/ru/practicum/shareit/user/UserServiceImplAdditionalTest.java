package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplAdditionalTest {

    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> argumentCaptor;
    @InjectMocks
    private UserServiceImpl service;

    @Test
    void updateUser_whenUserRepositoryReturnEmptyOptional_thenNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserDto userDtoUpdate = makeUserDto("some@NewEmail.com", "Пётр Новый Иванов");
        assertThrows(NotFoundException.class,
                () -> service.updateUser(userDtoUpdate, 1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_whenUserHasNameByUpdateNull() {
        User user = getUsers().get(0);
        user.setName(null);
        user.setEmail(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        UserDto userDto = makeUserDto(null, null);

        when(userRepository.save(any())).thenReturn(user);
        service.updateUser(userDto, 1);
        verify(userRepository).save(argumentCaptor.capture());
        User userToSave = argumentCaptor.getValue();
        assertThat(userToSave.getName(), equalTo(userDto.getName()));
        assertThat(userToSave.getEmail(), equalTo(userDto.getEmail()));
    }

    private List<User> getUsers() {
        return new ArrayList<>(
                List.of(
                        User.builder()
                                .id(1)
                                .name("Имя пользователя 1")
                                .email("email1@email.com")
                                .build(),
                        User.builder()
                                .id(2)
                                .name("Имя пользователя 2")
                                .email("email2@email.com")
                                .build()
                )
        );
    }

    private UserDto makeUserDto(String email, String name) {
        return UserDto.builder()
                .email(email)
                .name(name)
                .build();
    }
}