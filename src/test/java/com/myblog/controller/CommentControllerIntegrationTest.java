package com.myblog.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.dto.CreateCommentRequest;
import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.UpdateCommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Transactional
    void testUpdateComment() throws Exception {

        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Test Post");
        postRequest.setText("Content");
        postRequest.setTags(Arrays.asList());

        String postResponse = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long postId = objectMapper.readTree(postResponse).get("id").asLong();

        CreateCommentRequest commentRequest = new CreateCommentRequest();
        commentRequest.setText("Original comment");
        commentRequest.setPostId(postId);

        String commentResponse = mockMvc.perform(post("/posts/" + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long commentId = objectMapper.readTree(commentResponse).get("id").asLong();

        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        updateRequest.setId(commentId);
        updateRequest.setText("Updated comment");
        updateRequest.setPostId(postId);

        mockMvc.perform(put("/posts/" + postId + "/comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testDeleteComment() throws Exception {

        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Test Post");
        postRequest.setText("Content");
        postRequest.setTags(Arrays.asList());

        String postResponse = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long postId = objectMapper.readTree(postResponse).get("id").asLong();

        CreateCommentRequest commentRequest = new CreateCommentRequest();
        commentRequest.setText("Test comment");
        commentRequest.setPostId(postId);

        String commentResponse = mockMvc.perform(post("/posts/" + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long commentId = objectMapper.readTree(commentResponse).get("id").asLong();

        mockMvc.perform(delete("/posts/" + postId + "/comments/" + commentId))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void shouldUsePostIdFromPathWhenCreatingComment() throws Exception {
        String postJson = """
            {
              "title": "Post for comment path test",
              "text": "Body",
              "tags": ["test"]
            }
            """;

        String createdPostResponse = mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode postNode = objectMapper.readTree(createdPostResponse);
        long postId = postNode.get("id").asLong();

        String commentJson = """
            {
              "postId": 999999,
              "text": "Comment with wrong body postId"
            }
            """;

        String createdCommentResponse = mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode commentNode = objectMapper.readTree(createdCommentResponse);
        assertThat(commentNode.get("postId").asLong()).isEqualTo(postId);
    }

    @Test
    @Transactional
    void shouldReturnNotFoundWhenCommentDoesNotBelongToPost() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String firstPostJson = """
            {
              "title": "First post",
              "text": "Body 1",
              "tags": ["one"]
            }
            """;

        String secondPostJson = """
            {
              "title": "Second post",
              "text": "Body 2",
              "tags": ["two"]
            }
            """;

        long firstPostId = objectMapper.readTree(
                mockMvc.perform(post("/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstPostJson))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        ).get("id").asLong();

        long secondPostId = objectMapper.readTree(
                mockMvc.perform(post("/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondPostJson))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        ).get("id").asLong();

        String commentJson = """
            {
              "postId": %d,
              "text": "Comment for first post"
            }
            """.formatted(firstPostId);

        long commentId = objectMapper.readTree(
                mockMvc.perform(post("/posts/{postId}/comments", firstPostId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(commentJson))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        ).get("id").asLong();

        mockMvc.perform(get("/posts/{postId}/comments/{commentId}", secondPostId, commentId))
                .andExpect(status().isNotFound());
    }
}
