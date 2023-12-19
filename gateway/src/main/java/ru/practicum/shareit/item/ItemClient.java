package ru.practicum.shareit.item;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoCreateUpdate;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(ItemDtoCreateUpdate itemDtoCreateUpdate, long userId) {
        return post("", userId, itemDtoCreateUpdate);
    }

    public ResponseEntity<Object> updateItem(ItemDtoCreateUpdate itemDtoCreateUpdate, long itemId, long userId) {
        return patch("/" + itemId, userId, itemDtoCreateUpdate);
    }

    public ResponseEntity<Object> saveComment(CommentDtoCreate commentDtoCreate, long itemId, long userId) {
        return post("/" + itemId + "/comment", userId, commentDtoCreate);
    }

    public ResponseEntity<Object> findItemDtoById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findItemsByUserId(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItem(String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }
}