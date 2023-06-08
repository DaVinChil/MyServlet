package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Repository
// Stub
public class PostRepository {
    private Map<Long, Post> history = new ConcurrentHashMap();
    private Set<Long> removed = new HashSet<>();
    private AtomicLong availableId = new AtomicLong(1);
    public List<Post> all() {
        Collection<Post> posts = history.values();
        posts.removeIf(post -> removed.contains(post.getId()));
        return new ArrayList<>(posts);
    }

    public Optional<Post> getById(long id) {
        Post post = history.get(id);
        return Optional.ofNullable(post == null || removed.contains(id) ? null : post);
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
            if(history.containsKey(post.getId()) && !removed.contains(post.getId())) {
                throw new NotFoundException();
            }
            history.put(post.getId(), post);
        }

        return post;
    }

    public void removeById(long id) {
        if(history.containsKey(id)){
            removed.add(id);
        }
    }
}
