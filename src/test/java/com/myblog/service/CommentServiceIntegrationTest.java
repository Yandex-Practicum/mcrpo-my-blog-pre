package com.myblog.service;

import com.myblog.dto.CreateCommentRequest;
import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.UpdateCommentRequest;
import com.myblog.model.Comment;
import com.myblog.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Test
    void shouldCreateCommentForPost() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Test post");
        postRequest.setText("Test text");
        postRequest.setTags(List.of("test"));

        Post post = postService.createPost(postRequest);

        CreateCommentRequest commentRequest = new CreateCommentRequest();
        commentRequest.setPostId(post.getId());
        commentRequest.setText("My comment");

        Comment createdComment = commentService.createComment(commentRequest);

        assertThat(createdComment.getId()).isNotNull();
        assertThat(createdComment.getPostId()).isEqualTo(post.getId());
        assertThat(createdComment.getText()).isEqualTo("My comment");
    }

    @Test
    void shouldUpdateComment() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Post for update comment");
        postRequest.setText("Body");
        postRequest.setTags(List.of("update"));

        Post post = postService.createPost(postRequest);

        CreateCommentRequest createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setPostId(post.getId());
        createCommentRequest.setText("Old text");

        Comment createdComment = commentService.createComment(createCommentRequest);

        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        updateRequest.setText("New text");

        Comment updatedComment = commentService.updateComment(createdComment.getId(), updateRequest);

        assertThat(updatedComment.getId()).isEqualTo(createdComment.getId());
        assertThat(updatedComment.getText()).isEqualTo("New text");
    }

    @Test
    void shouldDeleteComment() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Post for delete comment");
        postRequest.setText("Body");
        postRequest.setTags(List.of("delete"));

        Post post = postService.createPost(postRequest);

        CreateCommentRequest createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setPostId(post.getId());
        createCommentRequest.setText("Comment to delete");

        Comment createdComment = commentService.createComment(createCommentRequest);

        commentService.deleteComment(createdComment.getId());

        Optional<Comment> deletedComment = commentService.getCommentById(createdComment.getId());
        assertThat(deletedComment).isEmpty();
    }

    @Test
    void shouldReturnCommentsByPostId() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Post with comments");
        postRequest.setText("Body");
        postRequest.setTags(List.of("comments"));

        Post post = postService.createPost(postRequest);

        CreateCommentRequest request1 = new CreateCommentRequest();
        request1.setPostId(post.getId());
        request1.setText("Comment 1");
        commentService.createComment(request1);

        CreateCommentRequest request2 = new CreateCommentRequest();
        request2.setPostId(post.getId());
        request2.setText("Comment 2");
        commentService.createComment(request2);

        List<Comment> comments = commentService.getCommentsByPostId(post.getId());

        assertThat(comments).hasSizeGreaterThanOrEqualTo(2);
        assertThat(comments).extracting(Comment::getText)
                .contains("Comment 1", "Comment 2");
    }
}
