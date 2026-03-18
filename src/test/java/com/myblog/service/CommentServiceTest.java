package com.myblog.service;

import com.myblog.dto.CreateCommentRequest;
import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.UpdateCommentRequest;
import com.myblog.model.Comment;
import com.myblog.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
 
@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Test
    void testGetCommentsByPostId() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Post for comments");
        postRequest.setText("Body");
        postRequest.setTags(Arrays.asList("t1"));

        Post post = postService.createPost(postRequest);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Test comment");
        request.setPostId(post.getId());
        commentService.createComment(request);

        List<Comment> result = commentService.getCommentsByPostId(post.getId());

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void testGetCommentById() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Post for comment");
        postRequest.setText("Body");
        postRequest.setTags(Arrays.asList("t1"));

        Post post = postService.createPost(postRequest);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Test comment");
        request.setPostId(post.getId());

        Comment created = commentService.createComment(request);

        Optional<Comment> result = commentService.getCommentById(created.getId());

        assertTrue(result.isPresent());
        assertEquals(created.getId(), result.get().getId());
    }

    @Test
    void testCreateComment() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("New comment");
        
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Post for create comment");
        postRequest.setText("Body");
        postRequest.setTags(Arrays.asList("t1"));

        Post post = postService.createPost(postRequest);
        request.setPostId(post.getId());

        Comment result = commentService.createComment(request);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(post.getId(), result.getPostId());
    }

    @Test
    void testUpdateComment() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Post for update comment");
        postRequest.setText("Body");
        postRequest.setTags(Arrays.asList("t1"));

        Post post = postService.createPost(postRequest);

        CreateCommentRequest createRequest = new CreateCommentRequest();
        createRequest.setText("Old text");
        createRequest.setPostId(post.getId());
        Comment created = commentService.createComment(createRequest);

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setText("Updated comment");

        Comment result = commentService.updateComment(created.getId(), request);

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("Updated comment", result.getText());
    }

    @Test
    void testUpdateCommentNotFound() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setText("Updated comment");

        assertThrows(IllegalArgumentException.class, () -> {
            commentService.updateComment(999L, request);
        });
    }

    @Test
    void testDeleteComment() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Post for delete comment");
        postRequest.setText("Body");
        postRequest.setTags(Arrays.asList("t1"));

        Post post = postService.createPost(postRequest);

        CreateCommentRequest createRequest = new CreateCommentRequest();
        createRequest.setText("To delete");
        createRequest.setPostId(post.getId());
        Comment created = commentService.createComment(createRequest);

        commentService.deleteComment(created.getId());

        Optional<Comment> deleted = commentService.getCommentById(created.getId());
        assertTrue(deleted.isEmpty());
    }
}

