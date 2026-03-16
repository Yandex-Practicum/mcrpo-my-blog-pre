package com.myblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.UpdatePostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/test-data.sql")
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getPosts_ShouldReturnPostsList() throws Exception {
        mockMvc.perform(get("/posts")
                .param("search", "")
                .param("pageNumber", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(2));
    }

    @Test
    void createPost_ShouldCreateNewPost() throws Exception {
        CreatePostRequest request = new CreatePostRequest(
            "New Post",
            "New Content",
            List.of("test")
        );

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New Post"))
                .andExpect(jsonPath("$.tags[0]").value("test"));
    }

    @Test
    void updatePost_ShouldUpdateExistingPost() throws Exception {
        // Сначала получим ID первого поста через поиск
        String content = mockMvc.perform(get("/posts")
                .param("search", "First Post")
                .param("pageNumber", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Извлекаем ID из JSON (упрощенно - в реальном тесте лучше использовать JSONPath)
        Long firstPostId = 1L; // В тестовых данных это будет 1

        UpdatePostRequest request = new UpdatePostRequest(
            firstPostId,
            "Updated Title",
            "Updated Content",
            List.of("updated")
        );

        mockMvc.perform(put("/posts/{id}", firstPostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.text").value("Updated Content"))
                .andExpect(jsonPath("$.tags[0]").value("updated"));
    }

    @Test
    void deletePost_ShouldRemovePost() throws Exception {
        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void incrementLikes_ShouldIncreaseCount() throws Exception {
        mockMvc.perform(post("/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(content().string("6")); // Было 5 + 1
    }

    @Test
    void decrementLikes_ShouldDecreaseCount() throws Exception {
        mockMvc.perform(delete("/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(content().string("4")); // Было 5 - 1
    }

    @Test
    void getPostById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/posts/999"))
                .andExpect(status().isNotFound());
    }
}
