package com.back.domain.post.post.controller;

import com.back.BaseTest;
import com.back.domain.post.post.document.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PostControllerTests extends BaseTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /api/v1/posts - 실패 (title 누락)")
    void t1() throws Exception {
        mockMvc.perform(
            post("/api/v1/posts")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.CreatePostRequest(
                                null,
                                "Test Content",
                                "Test Author"
                        )
                    )
                )
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/posts - 성공")
    void t2() throws Exception {
        mockMvc.perform(
            post("/api/v1/posts")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.CreatePostRequest(
                        "Test Title",
                        "Test Content",
                        "Test Author"
                        )
                    )
                )
            ).andExpect(status().isCreated())
                .andExpect(jsonPath("title").value("Test Title"))
                .andExpect(jsonPath("title").value("Test Title"))
                .andExpect(jsonPath("title").value("Test Title"))
                .andExpect(jsonPath("id").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/v1/posts - 성공")
    void t3() throws Exception {
        mockMvc.perform(
            get("/api/v1/posts")
                    .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/posts/{id} - 실패")
    void t4() throws Exception {
        mockMvc.perform(
            get("/api/v1/posts/{id}", "nonexistent-id")
                    .contentType("application/json")
                ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/posts/{id} - 성공")
    void t5() throws Exception {
        // 먼저 포스트를 생성
        String response = mockMvc.perform(
            post("/api/v1/posts")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.CreatePostRequest(
                                "Test Title for GetById",
                                "Test Content for GetById",
                                "Test Author for GetById"
                        )
                    )
                )
            ).andExpect(status().isCreated())
            .andReturn().getResponse()
            .getContentAsString();

        Post createdPost = objectMapper.readValue(response, Post.class);

        mockMvc.perform(get("/api/v1/posts/{id}", createdPost.getId())
                        .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("id").value(createdPost.getId()))
                .andExpect(jsonPath("title").value("Test Title for GetById"))
                .andExpect(jsonPath("content").value("Test Content for GetById"))
                .andExpect(jsonPath("author").value("Test Author for GetById"));
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{id} - 실패")
    void t6() throws Exception {
        mockMvc.perform(
            put("/api/v1/posts/{id}", "nonexistent-id")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.UpdatePostRequest(
                                "Updated Title",
                                "Updated Content"
                        )
                    )
                )
            ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{id} - 실패")
    void t7() throws Exception {
        // 먼저 포스트를 생성
        String response = mockMvc.perform(
            post("/api/v1/posts")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.CreatePostRequest(
                                "Test Title for Update Failure",
                                "Test Content for Update Failure",
                                "Test Author for Update Failure"
                        )
                    )
                )
            ).andExpect(status().isCreated())
            .andReturn().getResponse()
            .getContentAsString();

        Post createdPost = objectMapper.readValue(response, Post.class);

        // 이제 유효하지 않은 업데이트 요청을 보냄 (빈 제목)
        mockMvc.perform(
            put("/api/v1/posts/{id}", createdPost.getId())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.UpdatePostRequest(
                                "",
                                "Updated Content"
                        )
                    )
                )
            ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{id} - 성공")
    void t8() throws Exception {
        // 먼저 포스트를 생성
        String response = mockMvc.perform(
            post("/api/v1/posts")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.CreatePostRequest(
                                "Test Title for Update Success",
                                "Test Content for Update Success",
                                "Test Author for Update Success"
                        )
                    )
                )
            ).andExpect(status().isCreated())
            .andReturn().getResponse()
            .getContentAsString();
        Post createdPost = objectMapper.readValue(response, Post.class);
        // 이제 업데이트 요청을 보냄
        mockMvc.perform(
            put("/api/v1/posts/{id}", createdPost.getId())
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.UpdatePostRequest(
                                "Updated Title",
                                "Updated Content"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("id").value(createdPost.getId()))
            .andExpect(jsonPath("title").value("Updated Title"))
            .andExpect(jsonPath("content").value("Updated Content"));
    }

    @Test
    @DisplayName("DELETE /api/v1/posts/{id} - 실패")
    void t9() throws Exception {
        mockMvc.perform(
                delete("/api/v1/posts/{id}", "nonexistent-id")
                        .contentType("application/json")
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/posts/{id} - 성공")
    void t10() throws Exception {
        // 먼저 포스트를 생성
        String response = mockMvc.perform(
            post("/api/v1/posts")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsBytes(
                        new PostController.CreatePostRequest(
                            "Test Title for Delete",
                            "Test Content for Delete",
                            "Test Author for Delete"
                        )
                    )
                )
            ).andExpect(status().isCreated())
            .andReturn().getResponse()
            .getContentAsString();
        Post createdPost = objectMapper.readValue(response, Post.class);
        // 이제 삭제 요청을 보냄
        mockMvc.perform(
                delete("/api/v1/posts/{id}", createdPost.getId())
                        .contentType("application/json")
        ).andExpect(status().isNoContent());
    }
}
