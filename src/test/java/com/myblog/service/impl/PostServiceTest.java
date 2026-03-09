package com.myblog.service.impl;

import com.myblog.dao.PostDao;
import com.myblog.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostDao postDao;

    @InjectMocks
    private PostServiceImpl postService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setText("Test Content");
        testPost.setLikesCount(5);
    }

    @Test
    void decrementLikes_Success_WhenPostExists() {
        when(postDao.findById(1L)).thenReturn(Optional.of(testPost));
        // Убрали дублирующий вызов when

        int result = postService.decrementLikes(1L);

        verify(postDao).decrementLikes(1L);
        assertEquals(5, result);
    }

    @Test
    void decrementLikes_ThrowsException_WhenPostNotFound() {
        when(postDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.decrementLikes(1L));
        verify(postDao, never()).decrementLikes(anyLong());
    }

    @Test
    void deletePost_Success_WhenPostExists() {
        when(postDao.findById(1L)).thenReturn(Optional.of(testPost));

        postService.deletePost(1L);

        verify(postDao).delete(1L);
    }

    @Test
    void deletePost_ThrowsException_WhenPostNotFound() {
        when(postDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.deletePost(1L));
        verify(postDao, never()).delete(anyLong());
    }
}