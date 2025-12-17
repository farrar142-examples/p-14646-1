package com.back.domain.post.post.repository;

import com.back.domain.post.post.document.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostRepository extends ElasticsearchRepository<Post,String> {
}
