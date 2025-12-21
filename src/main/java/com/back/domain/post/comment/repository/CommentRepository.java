package com.back.domain.post.comment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.back.domain.post.comment.document.Comment;

public interface CommentRepository extends ElasticsearchRepository<Comment,String> {
    List<Comment> findAll();

    List<Comment> findByPostId(String postId);

    Page<Comment> findByPostId(String postId, Pageable pageable);
}
