package com.myblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.dto.UpdateCommentRequest;
import com.myblog.model.Comment;
import com.myblog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void updateComment_Returns200_WhenCommentExists() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(1L);
        request.setText("Updated text");
        request.setPostId(1L);

        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setText("Updated text");
        updatedComment.setPostId(1L);

        when(commentService.updateComment(eq(1L), any(UpdateCommentRequest.class))).thenReturn(updatedComment);

        mockMvc.perform(put("/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateComment_Returns404_WhenCommentNotFound() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(1L);
        request.setText("Updated text");
        request.setPostId(1L);

        when(commentService.updateComment(eq(1L), any(UpdateCommentRequest.class)))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteComment_Returns200_WhenCommentExists() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/posts/1/comments/1"))
                .andExpect(status().isOk());

        verify(commentService).deleteComment(1L);
    }

    @Test
    void deleteComment_Returns404_WhenCommentNotFound() throws Exception {
        doThrow(new IllegalArgumentException()).when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/posts/1/comments/1"))
                .andExpect(status().isNotFound());
    }
}