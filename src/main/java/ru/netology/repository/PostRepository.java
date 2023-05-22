package ru.netology.repository;

import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// Stub
public class PostRepository {
    Map<Long, Post> history = new ConcurrentHashMap();
    AtomicLong availableId = new AtomicLong(1);
    public List<Post> all() {
        return new ArrayList<>(history.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.of(history.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            while(history.containsKey(availableId.get())){
                availableId.incrementAndGet();
            }
            Long id = availableId.getAndIncrement();
            post.setId(id);
            history.put(id, post);
        } else {
            history.put(post.getId(), post);
        }

        return post;
    }

    public void removeById(long id) {
        history.remove(id);
    }
}
