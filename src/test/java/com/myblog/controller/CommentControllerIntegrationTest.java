package com.myblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.config.DatabaseConfig;
import com.myblog.config.RootConfig;
import com.myblog.config.WebConfig;
import com.myblog.dto.CreateCommentRequest;
import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.UpdateCommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, WebConfig.class, DatabaseConfig.class})
@WebAppConfiguration
@Transactional
class CommentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        jdbcTemplate.execute("DELETE FROM post_images");
        jdbcTemplate.execute("DELETE FROM post_tags");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM tags");
        jdbcTemplate.execute("DELETE FROM posts");
    }

    @Test
    void testGetComments() throws Exception {
        Long postId = createTestPost();

        mockMvc.perform(get("/api/posts/" + postId + "/comments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCreateComment() throws Exception {
        Long postId = createTestPost();

        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Test comment");
        request.setPostId(postId);

        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.text").value("Test comment"))
            .andExpect(jsonPath("$.postId").value(postId));
    }

    @Test
    void testGetCommentById() throws Exception {
        Long postId = createTestPost();
        Long commentId = createTestComment(postId, "Test comment");

        mockMvc.perform(get("/api/posts/" + postId + "/comments/" + commentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(commentId))
            .andExpect(jsonPath("$.text").value("Test comment"));
    }

    @Test
    void testUpdateComment() throws Exception {
        Long postId = createTestPost();
        Long commentId = createTestComment(postId, "Original comment");

        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        updateRequest.setId(commentId);
        updateRequest.setText("Updated comment");
        updateRequest.setPostId(postId);

        mockMvc.perform(put("/api/posts/" + postId + "/comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(commentId))
            .andExpect(jsonPath("$.text").value("Updated comment"));
    }

    @Test
    void testUpdateCommentNotFound() throws Exception {
        Long postId = createTestPost();

        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        updateRequest.setId(999L);
        updateRequest.setText("Updated comment");
        updateRequest.setPostId(postId);

        mockMvc.perform(put("/api/posts/" + postId + "/comments/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteComment() throws Exception {
        Long postId = createTestPost();
        Long commentId = createTestComment(postId, "Comment to delete");

        mockMvc.perform(delete("/api/posts/" + postId + "/comments/" + commentId))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/" + postId + "/comments/" + commentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePostCascadesComments() throws Exception {
        Long postId = createTestPost();
        Long commentId = createTestComment(postId, "Comment to cascade delete");

        mockMvc.perform(delete("/api/posts/" + postId))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/" + postId + "/comments/" + commentId))
            .andExpect(status().isNotFound());
    }

    private Long createTestPost() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Test Post");
        request.setText("Test content");
        request.setTags(Arrays.asList());

        String response = mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long createTestComment(Long postId, String text) throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText(text);
        request.setPostId(postId);

        String response = mockMvc.perform(post("/api/posts/" + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}

