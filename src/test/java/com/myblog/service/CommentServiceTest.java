package com.myblog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.myblog.dto.CreateCommentRequest;
import com.myblog.dto.UpdateCommentRequest;
import com.myblog.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.myblog.MyBlogApplication;
import com.myblog.dao.CommentDao;

@SpringBootTest(classes = MyBlogApplication.class)
class CommentServiceTest {

    @MockBean
    private CommentDao commentDao;

    @Autowired
    private CommentService commentService;

    private Comment testComment;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setId(1L);
        testComment.setText("Test comment");
        testComment.setPostId(1L);
    }

    @Test
    void getCommentsByPostIdReturnsComments() {
        when(commentDao.findByPostId(1L)).thenReturn(List.of(testComment));

        List<Comment> result = commentService.getCommentsByPostId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testComment.getId(), result.get(0).getId());
        verify(commentDao).findByPostId(1L);
    }

    @Test
    void getCommentByIdReturnsEntity() {
        when(commentDao.findById(1L)).thenReturn(Optional.of(testComment));

        Optional<Comment> result = commentService.getCommentById(1L);

        assertTrue(result.isPresent());
        assertEquals(testComment.getId(), result.get().getId());
        verify(commentDao).findById(1L);
    }

    @Test
    void createCommentDelegatesToDao() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("New comment");
        request.setPostId(1L);

        when(commentDao.create(any(Comment.class))).thenReturn(testComment);

        Comment result = commentService.createComment(request);

        assertEquals(testComment.getId(), result.getId());
        verify(commentDao).create(any(Comment.class));
    }

    @Test
    void updateCommentThrowsWhenEntityMissing() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setText("Updated comment");
        request.setPostId(1L);

        when(commentDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(999L, request));
        verify(commentDao).findById(999L);
        verify(commentDao, never()).update(any(Comment.class));
    }
}
