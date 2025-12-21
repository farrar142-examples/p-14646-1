package com.back.domain.post.comment.controller;

import com.back.domain.post.comment.document.Comment;
import com.back.domain.post.comment.service.CommentService;
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

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    public record CreateCommentRequest(
            @NotBlank(message = "Content must not be blank")
            @Size(max = 500, min = 1)
            String content,
            @NotBlank(message = "Author must not be blank")
            @Size(max = 50, min = 1)
            String author
    ) {}

    @PostMapping
    public ResponseEntity<Comment> create(
            @PathVariable String postId,
            @RequestBody @Valid CreateCommentRequest request
    ) {
        Comment comment = commentService.create(
                postService.findById(postId),
                request.content,
                request.author
        );
        return ResponseEntity.status(201).body(comment);
    }

    @GetMapping
    public Page<Comment> findByPostId(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Post 존재 여부 확인
        postService.findById(postId);
        Pageable pageable = PageRequest.of(page, size);
        return commentService.findByPostId(postId, pageable);
    }

    @GetMapping("/{id}")
    public Comment findById(
            @PathVariable String postId,
            @PathVariable String id
    ) {
        // Post 존재 여부 확인
        postService.findById(postId);
        return commentService.findById(id);
    }

    public record UpdateCommentRequest(
            @NotBlank(message = "Content must not be blank")
            @Size(max = 500, min = 1)
            String content
    ) {}

    @PutMapping("/{id}")
    public Comment update(
            @PathVariable String postId,
            @PathVariable String id,
            @RequestBody @Valid UpdateCommentRequest request
    ) {
        // Post 존재 여부 확인
        postService.findById(postId);
        return commentService.update(id, request.content);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String postId,
            @PathVariable String id
    ) {
        // Post 존재 여부 확인
        postService.findById(postId);
        Comment comment = commentService.findById(id);
        commentService.delete(comment);
        return ResponseEntity.noContent().build();
    }
}
