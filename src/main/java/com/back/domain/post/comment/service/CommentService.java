package com.back.domain.post.comment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.back.domain.post.comment.document.Comment;
import com.back.domain.post.comment.repository.CommentRepository;
import com.back.domain.post.post.document.Post;
import com.back.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public long count() {
        return commentRepository.count();
    }

    public Comment create(Post post, String content, String author) {
        Comment comment = new Comment(post.getId(),content, author);
        return commentRepository.save(comment);
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Comment findById(String id) {
        return commentRepository.findById(id).orElseThrow(()->new NotFoundException("Comment not found with id: " + id));
    }

    public List<Comment> findByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    public Page<Comment> findByPostId(String postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    public Comment update(String id, String content) {
        Comment comment = findById(id);
        if (content != null){
            comment.setContent(content);
        }
        return commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
}
