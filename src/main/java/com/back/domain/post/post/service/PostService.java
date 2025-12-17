package com.back.domain.post.post.service;

import com.back.domain.post.post.document.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public Post create(String title, String content, String author) {
        Post post = new Post(title, content, author);
        return postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(String id) {
        return postRepository.findById(id).orElseThrow(()->new NotFoundException("Post not found with id: " + id));
    }

    public Post update(String id, String title, String content) {
        Post post = findById(id);
        if (title != null){
            post.setTitle(title);
        }
        if (content != null){
            post.setContent(content);
        }
        post.setLastModifiedAt(java.time.OffsetDateTime.now());
        return postRepository.save(post);
    }
}
