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

    // 내용 검색
    Page<Comment> findByPostIdAndContentContaining(String postId, String content, Pageable pageable);

    // 작성자 검색
    Page<Comment> findByPostIdAndAuthor(String postId, String author, Pageable pageable);

    // 내용 + 작성자 검색
    Page<Comment> findByPostIdAndContentContainingOrPostIdAndAuthor(
            String postId1, String content,
            String postId2, String author,
            Pageable pageable
    );
}
