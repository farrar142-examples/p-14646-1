package com.back.domain.post.comment.controller;

import com.back.BaseTest;
import com.back.domain.post.comment.document.Comment;
import com.back.domain.post.post.document.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class CommentControllerTests extends BaseTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Post createTestPost() throws Exception {
        String response = mockMvc.perform(
                post("/api/v1/posts")
                    .contentType("application/json")
                    .content(
                        objectMapper.writeValueAsBytes(
                            Map.of(
                                "title", "Test Post Title",
                                "content", "Test Post Content",
                                "author", "Test Post Author"
                            )
                        )
                    )
            ).andExpect(status().isCreated())
            .andReturn().getResponse()
            .getContentAsString();

        return objectMapper.readValue(response, Post.class);
    }

    @Test
    @DisplayName("POST /api/v1/posts/{postId}/comments - 실패 (content 누락)")
    void t1() throws Exception {
        Post post = createTestPost();
        mockMvc.perform(
            post("/api/v1/posts/{postId}/comments", post.getId())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "author", "Test Author"
                        )
                    )
                )
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/posts/{postId}/comments - 실패 (존재하지 않는 postId)")
    void t2() throws Exception {
        mockMvc.perform(
            post("/api/v1/posts/{postId}/comments", "nonexistent-post-id")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "content", "Test Content",
                            "author", "Test Author"
                        )
                    )
                )
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/posts/{postId}/comments - 성공")
    void t3() throws Exception {
        Post post = createTestPost();
        mockMvc.perform(
            post("/api/v1/posts/{postId}/comments", post.getId())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "content", "Test Comment Content",
                            "author", "Test Comment Author"
                        )
                    )
                )
        ).andExpect(status().isCreated())
            .andExpect(jsonPath("content").value("Test Comment Content"))
            .andExpect(jsonPath("author").value("Test Comment Author"))
            .andExpect(jsonPath("postId").value(post.getId()))
            .andExpect(jsonPath("id").isNotEmpty());
    }

    private Comment createTestComment(String postId) throws Exception {
        String response = mockMvc.perform(
            post("/api/v1/posts/{postId}/comments", postId)
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "content", "Test Comment Content",
                            "author", "Test Comment Author"
                        )
                    )
                )
        ).andExpect(status().isCreated())
            .andReturn().getResponse()
            .getContentAsString();

        return objectMapper.readValue(response, Comment.class);
    }

    @Test
    @DisplayName("GET /api/v1/posts/{postId}/comments - 실패 (존재하지 않는 postId)")
    void t4() throws Exception {
        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments", "nonexistent-post-id")
                .contentType("application/json")
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/posts/{postId}/comments - 성공")
    void t5() throws Exception {
        Post post = createTestPost();
        createTestComment(post.getId());
        createTestComment(post.getId());

        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments", post.getId())
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/posts/{postId}/comments/{id} - 실패 (존재하지 않는 commentId)")
    void t6() throws Exception {
        Post post = createTestPost();
        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments/{id}", post.getId(), "nonexistent-comment-id")
                .contentType("application/json")
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/posts/{postId}/comments/{id} - 성공")
    void t7() throws Exception {
        Post post = createTestPost();
        Comment comment = createTestComment(post.getId());

        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments/{id}", post.getId(), comment.getId())
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andExpect(jsonPath("id").value(comment.getId()))
            .andExpect(jsonPath("content").value("Test Comment Content"))
            .andExpect(jsonPath("author").value("Test Comment Author"));
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{postId}/comments/{id} - 실패 (존재하지 않는 commentId)")
    void t8() throws Exception {
        Post post = createTestPost();
        mockMvc.perform(
            put("/api/v1/posts/{postId}/comments/{id}", post.getId(), "nonexistent-comment-id")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "content", "Updated Content"
                        )
                    )
                )
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{postId}/comments/{id} - 성공")
    void t9() throws Exception {
        Post post = createTestPost();
        Comment comment = createTestComment(post.getId());

        mockMvc.perform(
            put("/api/v1/posts/{postId}/comments/{id}", post.getId(), comment.getId())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "content", "Updated Comment Content"
                        )
                    )
                )
        ).andExpect(status().isOk())
            .andExpect(jsonPath("id").value(comment.getId()))
            .andExpect(jsonPath("content").value("Updated Comment Content"));
    }

    @Test
    @DisplayName("DELETE /api/v1/posts/{postId}/comments/{id} - 실패 (존재하지 않는 commentId)")
    void t10() throws Exception {
        Post post = createTestPost();
        mockMvc.perform(
            delete("/api/v1/posts/{postId}/comments/{id}", post.getId(), "nonexistent-comment-id")
                .contentType("application/json")
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/posts/{postId}/comments/{id} - 성공")
    void t11() throws Exception {
        Post post = createTestPost();
        Comment comment = createTestComment(post.getId());

        mockMvc.perform(
            delete("/api/v1/posts/{postId}/comments/{id}", post.getId(), comment.getId())
                .contentType("application/json")
        ).andExpect(status().isNoContent());

        // 삭제 후 재조회 시 404 응답 확인
        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments/{id}", post.getId(), comment.getId())
                .contentType("application/json")
        ).andExpect(status().isNotFound());
    }
}
