package com.myblog.service;

import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.PostListResponse;
import com.myblog.dto.UpdatePostRequest;
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
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void testGetPosts() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Test Post");
        request.setText("Test content");
        request.setTags(Arrays.asList("tag1", "tag2"));

        postService.createPost(request);

        PostListResponse response = postService.getPosts("", 1, 10);

        assertNotNull(response);
        assertNotNull(response.getPosts());
        assertTrue(response.getPosts().size() >= 1);
        assertTrue(response.getLastPage() >= 0);
    }

    @Test
    void testGetPostById() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Test Post");
        request.setText("Test content");
        request.setTags(Arrays.asList("tag1", "tag2"));

        Post created = postService.createPost(request);

        Optional<Post> result = postService.getPostById(created.getId());

        assertTrue(result.isPresent());
        assertEquals(created.getId(), result.get().getId());
        assertEquals("Test Post", result.get().getTitle());
    }

    @Test
    void testCreatePost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("New Post");
        request.setText("New content");
        request.setTags(Arrays.asList("tag1"));

        Post result = postService.createPost(request);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("New Post", result.getTitle());
    }

    @Test
    void testUpdatePost() {
        UpdatePostRequest request = new UpdatePostRequest();
        CreatePostRequest createRequest = new CreatePostRequest();
        createRequest.setTitle("Old title");
        createRequest.setText("Old text");
        createRequest.setTags(Arrays.asList("tag1"));

        Post created = postService.createPost(createRequest);

        request.setTitle("Updated Post");
        request.setText("Updated content");
        request.setTags(Arrays.asList("tag1"));

        Post result = postService.updatePost(created.getId(), request);

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("Updated Post", result.getTitle());
    }

    @Test
    void testUpdatePostNotFound() {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle("Updated Post");
        request.setText("Updated content");
        request.setTags(Arrays.asList("tag1"));

        assertThrows(IllegalArgumentException.class, () -> {
            postService.updatePost(999L, request);
        });
    }

    @Test
    void testDeletePost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Delete Post");
        request.setText("Delete content");
        request.setTags(Arrays.asList("tag1"));

        Post created = postService.createPost(request);
        postService.deletePost(created.getId());

        Optional<Post> deleted = postService.getPostById(created.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testIncrementLikes() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Like Post");
        request.setText("Like content");
        request.setTags(Arrays.asList("tag1"));

        Post created = postService.createPost(request);

        int likesCount = postService.incrementLikes(created.getId());
        assertEquals(1, likesCount);
    }

    @Test
    void testDecrementLikes() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Like Post");
        request.setText("Like content");
        request.setTags(Arrays.asList("tag1"));

        Post created = postService.createPost(request);
        postService.incrementLikes(created.getId());
        postService.incrementLikes(created.getId());
        postService.incrementLikes(created.getId());

        int likesCount = postService.decrementLikes(created.getId());

        assertEquals(2, likesCount);
    }
}

