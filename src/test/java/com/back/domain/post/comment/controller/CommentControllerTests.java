package com.back.domain.post.comment.controller;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.back.BaseTest;
import com.back.domain.post.comment.document.Comment;
import com.back.domain.post.post.document.Post;

import tools.jackson.databind.ObjectMapper;

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
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(10));
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

    @Test
    @DisplayName("GET /api/v1/posts/{postId}/comments - Pagination 파라미터 테스트")
    void t12() throws Exception {
        Post post = createTestPost();

        // 15개의 Comment 생성
        for (int i = 0; i < 15; i++) {
            mockMvc.perform(
                post("/api/v1/posts/{postId}/comments", post.getId())
                    .contentType("application/json")
                    .content(
                        objectMapper.writeValueAsBytes(
                            Map.of(
                                "content", "Pagination Test Content " + i,
                                "author", "Pagination Test Author"
                            )
                        )
                    )
            ).andExpect(status().isCreated());
        }

        // 첫 번째 페이지 조회 (size=5)
        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments", post.getId())
                .param("page", "0")
                .param("size", "5")
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(5))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(5));

        // 두 번째 페이지 조회 (size=5)
        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments", post.getId())
                .param("page", "1")
                .param("size", "5")
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.pageable.pageNumber").value(1))
            .andExpect(jsonPath("$.pageable.pageSize").value(5));
    }

    @Test
    @DisplayName("GET /api/v1/posts/{postId}/comments/search - 내용 검색")
    void t13() throws Exception {
        Post post = createTestPost();

        // 검색용 Comment 생성
        mockMvc.perform(
            post("/api/v1/posts/{postId}/comments", post.getId())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "content", "UniqueSearchableContent",
                            "author", "SearchAuthor"
                        )
                    )
                )
        ).andExpect(status().isCreated());

        // 내용으로 검색
        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments/search", post.getId())
                .param("keyword", "UniqueSearchableContent")
                .param("searchType", "content")
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].content").value("UniqueSearchableContent"));
    }

    @Test
    @DisplayName("GET /api/v1/posts/{postId}/comments/search - 작성자 검색")
    void t14() throws Exception {
        Post post = createTestPost();

        // 검색용 Comment 생성
        mockMvc.perform(
            post("/api/v1/posts/{postId}/comments", post.getId())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "content", "Content",
                            "author", "UniqueSearchableAuthor"
                        )
                    )
                )
        ).andExpect(status().isCreated());

        // 작성자로 검색
        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments/search", post.getId())
                .param("keyword", "UniqueSearchableAuthor")
                .param("searchType", "author")
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].author").value("UniqueSearchableAuthor"));
    }

    @Test
    @DisplayName("GET /api/v1/posts/{postId}/comments/search - 내용+작성자 검색 (기본값)")
    void t15() throws Exception {
        Post post = createTestPost();

        // 검색용 Comment 생성
        mockMvc.perform(
            post("/api/v1/posts/{postId}/comments", post.getId())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        Map.of(
                            "content", "ContentAndAuthorSearchTest",
                            "author", "TestAuthor"
                        )
                    )
                )
        ).andExpect(status().isCreated());

        // 내용+작성자로 검색 (기본값)
        mockMvc.perform(
            get("/api/v1/posts/{postId}/comments/search", post.getId())
                .param("keyword", "ContentAndAuthorSearchTest")
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].content").value("ContentAndAuthorSearchTest"));
    }
}
