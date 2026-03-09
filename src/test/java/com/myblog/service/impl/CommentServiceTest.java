package com.myblog.service.impl;

import com.myblog.dao.CommentDao;
import com.myblog.dto.UpdateCommentRequest;
import com.myblog.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentDao commentDao;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment testComment;
    private UpdateCommentRequest updateRequest;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setId(1L);
        testComment.setText("Original text");
        testComment.setPostId(1L);

        updateRequest = new UpdateCommentRequest();
        updateRequest.setId(1L);
        updateRequest.setText("Updated text");
        updateRequest.setPostId(1L);
    }

    @Test
    void updateComment_Success() {
        when(commentDao.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentDao.update(any(Comment.class))).thenReturn(testComment);

        Comment result = commentService.updateComment(1L, updateRequest);

        assertEquals("Updated text", result.getText());
        verify(commentDao).update(any(Comment.class));
    }

    @Test
    void updateComment_ThrowsException_WhenCommentNotFound() {
        when(commentDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(1L, updateRequest));
        verify(commentDao, never()).update(any());
    }

    @Test
    void updateComment_ThrowsException_WhenPostIdMismatch() {
        testComment.setPostId(2L);
        when(commentDao.findById(1L)).thenReturn(Optional.of(testComment));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(1L, updateRequest));
        verify(commentDao, never()).update(any());
    }

    @Test
    void deleteComment_Success() {
        when(commentDao.findById(1L)).thenReturn(Optional.of(testComment));
        doNothing().when(commentDao).delete(1L);

        commentService.deleteComment(1L);

        verify(commentDao).delete(1L);
    }

    @Test
    void deleteComment_ThrowsException_WhenCommentNotFound() {
        when(commentDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(1L));
        verify(commentDao, never()).delete(anyLong());
    }
}