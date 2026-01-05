package com.back.domain.post.repository;

import com.back.domain.post.document.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PostRepository extends ElasticsearchRepository<Post,String> {
    List<Post> findAll();
}
