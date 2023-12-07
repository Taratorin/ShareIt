package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(User user);

    List<ItemRequest> findAllByRequestorNotOrderByCreatedDesc(User requestor, Pageable pageable);



}
