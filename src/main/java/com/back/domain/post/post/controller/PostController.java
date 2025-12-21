package com.back.domain.post.post.controller;

import com.back.domain.post.post.document.Post;
import com.back.domain.post.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    record CreatePostRequest(
            @NotBlank(message = "Title must not be blank")
            @Size(max = 100, min = 1)
            String title,
            @NotBlank(message = "Content must not be blank")
            String content,
            @NotBlank(message = "Author must not be blank")
            String author
    ){}

    @PostMapping
    public ResponseEntity<Post> create(
            @RequestBody @Valid CreatePostRequest request
    ){
        Post post = postService.create(
                request.title,
                request.content,
                request.author
        );
        return ResponseEntity.status(201).body(post);
    }

    @RequestMapping
    public Page<Post> findAll(@RequestParam (defaultValue = "0") int page,
                              @RequestParam (defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page,size);
        return postService.findAll(pageable);
    }

    @RequestMapping("/{id}")
    public Post findById(@PathVariable String id) {
        return postService.findById(id);
    }

    record UpdatePostRequest(
            @NotBlank(message = "Title must not be blank")
            @Size(max = 100, min = 1)
            String title,
            @NotBlank(message = "Content must not be blank")
            String content
    ){}

    @PutMapping("/{id}")
    public Post update(
            @PathVariable String id,
            @RequestBody @Valid UpdatePostRequest request
    ) {
        return postService.update(
                id,
                request.title,
                request.content
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
