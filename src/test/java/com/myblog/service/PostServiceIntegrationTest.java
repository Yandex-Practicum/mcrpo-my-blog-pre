package com.myblog.service;

import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.PostListResponse;
import com.myblog.dto.UpdatePostRequest;
import com.myblog.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Test
    void shouldCreatePost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Created post");
        request.setText("Created text");
        request.setTags(List.of("spring", "boot"));

        Post post = postService.createPost(request);

        assertThat(post.getId()).isNotNull();
        assertThat(post.getTitle()).isEqualTo("Created post");
        assertThat(post.getText()).isEqualTo("Created text");
        assertThat(post.getTags())
                .contains("spring", "boot");
    }

    @Test
    void shouldUpdatePost() {
        CreatePostRequest createRequest = new CreatePostRequest();
        createRequest.setTitle("Old title");
        createRequest.setText("Old text");
        createRequest.setTags(List.of("old"));

        Post createdPost = postService.createPost(createRequest);

        UpdatePostRequest updateRequest = new UpdatePostRequest();
        updateRequest.setTitle("New title");
        updateRequest.setText("New text");
        updateRequest.setTags(List.of("new", "updated"));

        Post updatedPost = postService.updatePost(createdPost.getId(), updateRequest);

        assertThat(updatedPost.getId()).isEqualTo(createdPost.getId());
        assertThat(updatedPost.getTitle()).isEqualTo("New title");
        assertThat(updatedPost.getText()).isEqualTo("New text");
        assertThat(updatedPost.getTags())
                .contains("new", "updated");
    }

    @Test
    void shouldIncrementAndDecrementLikes() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Like post");
        request.setText("Like text");
        request.setTags(List.of("likes"));

        Post post = postService.createPost(request);

        int likesAfterIncrement = postService.incrementLikes(post.getId());
        int likesAfterSecondIncrement = postService.incrementLikes(post.getId());
        int likesAfterDecrement = postService.decrementLikes(post.getId());

        assertThat(likesAfterIncrement).isEqualTo(1);
        assertThat(likesAfterSecondIncrement).isEqualTo(2);
        assertThat(likesAfterDecrement).isEqualTo(1);
    }

    @Test
    void shouldSaveAndLoadImage() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Image post");
        request.setText("Image text");
        request.setTags(List.of("image"));

        Post post = postService.createPost(request);

        byte[] imageBytes = "fake-image".getBytes(StandardCharsets.UTF_8);
        String contentType = "image/png";

        postService.saveImage(post.getId(), imageBytes, contentType);

        Optional<byte[]> loadedImage = postService.getImage(post.getId());
        Optional<String> loadedContentType = postService.getImageContentType(post.getId());

        assertThat(loadedImage).isPresent();
        assertThat(loadedImage.get()).isEqualTo(imageBytes);
        assertThat(loadedContentType).contains("image/png");
    }

    @Test
    void shouldDeletePost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Delete post");
        request.setText("Delete text");
        request.setTags(List.of("delete"));

        Post post = postService.createPost(request);

        postService.deletePost(post.getId());

        Optional<Post> deletedPost = postService.getPostById(post.getId());
        assertThat(deletedPost).isEmpty();
    }

    @Test
    void shouldReturnPagedPosts() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Searchable post");
        request.setText("Body");
        request.setTags(List.of("search"));

        postService.createPost(request);

        PostListResponse response = postService.getPosts("", 1, 10);

        assertThat(response).isNotNull();
        assertThat(response.getPosts()).isNotEmpty();
        assertThat(response.getLastPage()).isGreaterThanOrEqualTo(1);
    }
}
