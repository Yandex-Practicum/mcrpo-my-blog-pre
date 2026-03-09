package com.myblog.controller;

import com.myblog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void removeLike_Returns200_WhenPostExists() throws Exception {
        when(postService.decrementLikes(1L)).thenReturn(4);

        mockMvc.perform(delete("/posts/1/likes"))
                .andExpect(status().isOk());

        verify(postService).decrementLikes(1L);
    }

    @Test
    void removeLike_Returns404_WhenPostNotFound() throws Exception {
        when(postService.decrementLikes(1L)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(delete("/posts/1/likes"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePost_Returns200_WhenPostExists() throws Exception {
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isOk());

        verify(postService).deletePost(1L);
    }

    @Test
    void deletePost_Returns404_WhenPostNotFound() throws Exception {
        doThrow(new IllegalArgumentException()).when(postService).deletePost(1L);

        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isNotFound());
    }
}