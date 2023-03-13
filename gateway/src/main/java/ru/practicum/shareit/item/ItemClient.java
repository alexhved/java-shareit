package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }


    public ResponseEntity<Object> save(long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemRequestDto itemRequestDto) {
        return patch("/" + itemId, userId, itemRequestDto);
    }

    public ResponseEntity<Object> findById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findAllByUserId(long userId, Integer from, Integer size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> searchItem(long userId, String text, Integer from, Integer size) {
        Map<String, Object> params = Map.of("text", text,
                "from", from,
                "size", size);
        return get("/search?from={from}&size={size}&text={text}", userId, params);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentRequestDto commentRequestDto) {
        return post("/" + itemId + "/comment", userId, commentRequestDto);
    }
}
