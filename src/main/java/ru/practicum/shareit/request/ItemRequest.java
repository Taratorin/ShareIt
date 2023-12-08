package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@ToString
@Builder
@Table(name = "requests", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
    private LocalDateTime created;
}
