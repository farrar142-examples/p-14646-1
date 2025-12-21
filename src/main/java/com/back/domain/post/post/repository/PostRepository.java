package com.back.domain.post.post.repository;

import com.back.domain.post.post.document.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PostRepository extends ElasticsearchRepository<Post,String> {
    List<Post> findAll();
    Page<Post> findAll(Pageable pageable);

    // 제목 검색
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    // 내용 검색
    Page<Post> findByContentContaining(String content, Pageable pageable);

    // 제목 + 내용 검색
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
