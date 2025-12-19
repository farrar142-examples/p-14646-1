package com.back.domain.post.comment.controller;

import com.back.domain.post.comment.document.Comment;
import com.back.domain.post.comment.service.CommentService;
import com.back.domain.post.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Comment> findByPostId(@PathVariable String postId) {
        // Post 존재 여부 확인
        postService.findById(postId);
        return commentService.findByPostId(postId);
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
}
