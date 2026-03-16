package com.myblog.service;

import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.PostListResponse;
import com.myblog.dto.UpdatePostRequest;
import com.myblog.model.Post;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Важно!
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // Перед каждым тестом
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    @Order(1)
    void createPost_ShouldCreateNewPost() {
        CreatePostRequest request = new CreatePostRequest(
            "Test Title",
            "Test Content",
            List.of("java", "spring")
        );

        Post created = postService.createPost(request);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Test Title");
        assertThat(created.getText()).isEqualTo("Test Content");
        assertThat(created.getTags()).contains("java", "spring");
    }

    @Test
    @Order(2)
    void getPostById_ShouldReturnPost() {
        // Находим пост по заголовку, так как ID теперь динамические
        PostListResponse response = postService.getPosts("First Post", 1, 10);
        assertThat(response.getPosts()).isNotEmpty();
        
        Long firstPostId = response.getPosts().get(0).getId();
        Optional<Post> post = postService.getPostById(firstPostId);

        assertThat(post).isPresent();
        assertThat(post.get().getTitle()).isEqualTo("First Post");
        assertThat(post.get().getText()).isEqualTo("Content of first post");
        assertThat(post.get().getTags()).contains("java", "spring");
        assertThat(post.get().getLikesCount()).isEqualTo(5);
    }

    @Test
    @Order(3)
    void getPosts_ShouldReturnPaginatedResults() {
        PostListResponse response = postService.getPosts("", 1, 10);

        assertThat(response).isNotNull();
        assertThat(response.getPosts()).isNotEmpty();
        assertThat(response.getPosts().size()).isLessThanOrEqualTo(10);
        assertThat(response.getLastPage()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(4)
    void updatePost_ShouldUpdateExistingPost() {
        // Находим ID первого поста
        PostListResponse response = postService.getPosts("First Post", 1, 10);
        assertThat(response.getPosts()).isNotEmpty();
        Long firstPostId = response.getPosts().get(0).getId();

        UpdatePostRequest request = new UpdatePostRequest(
            firstPostId,
            "Updated Title",
            "Updated Content",
            List.of("updated")
        );

        Post updated = postService.updatePost(firstPostId, request);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(firstPostId);
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getText()).isEqualTo("Updated Content");
        assertThat(updated.getTags()).contains("updated");
    }

    @Test
    @Order(5)
    void deletePost_ShouldRemovePost() {
        // Находим ID первого поста
        PostListResponse response = postService.getPosts("First Post", 1, 10);
        assertThat(response.getPosts()).isNotEmpty();
        Long firstPostId = response.getPosts().get(0).getId();

        postService.deletePost(firstPostId);

        Optional<Post> deleted = postService.getPostById(firstPostId);
        assertThat(deleted).isEmpty();
    }

    @Test
    @Order(6)
    void incrementLikes_ShouldIncreaseLikeCount() {
        // Находим ID второго поста
        PostListResponse response = postService.getPosts("Second Post", 1, 10);
        assertThat(response.getPosts()).isNotEmpty();
        Long secondPostId = response.getPosts().get(0).getId();

        int likes = postService.incrementLikes(secondPostId);
        
        Optional<Post> post = postService.getPostById(secondPostId);
        assertThat(post).isPresent();
        assertThat(post.get().getLikesCount()).isEqualTo(likes);
        assertThat(likes).isEqualTo(4); // Было 3, стало 4
    }
}
