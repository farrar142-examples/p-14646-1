package com.back.domain.post.post.controller;

import com.back.BaseTest;
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
}
