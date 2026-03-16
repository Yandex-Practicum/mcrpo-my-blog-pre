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

import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.PostListResponse;
import com.myblog.dto.UpdatePostRequest;
import com.myblog.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.myblog.MyBlogApplication;
import com.myblog.dao.PostDao;

@SpringBootTest(classes = MyBlogApplication.class)
class PostServiceTest {

    @MockBean
    private PostDao postDao;

    @Autowired
    private PostService postService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setText("Test content");
        testPost.setTags(List.of("tag1", "tag2"));
        testPost.setLikesCount(0);
        testPost.setCommentsCount(0);
    }

    @Test
    void getPostsReturnsPaginationData() {
        when(postDao.findAll("", 1, 10)).thenReturn(List.of(testPost));
        when(postDao.getTotalCount("")).thenReturn(1);

        PostListResponse response = postService.getPosts("", 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getPosts().size());
        assertEquals(testPost.getId(), response.getPosts().get(0).getId());
        assertEquals(1, response.getLastPage());
        verify(postDao).findAll("", 1, 10);
        verify(postDao).getTotalCount("");
    }

    @Test
    void getPostByIdReturnsEntity() {
        when(postDao.findById(1L)).thenReturn(Optional.of(testPost));

        Optional<Post> result = postService.getPostById(1L);

        assertTrue(result.isPresent());
        assertEquals(testPost.getTitle(), result.get().getTitle());
        verify(postDao).findById(1L);
    }

    @Test
    void createPostDelegatesToDao() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("New Post");
        request.setText("New content");
        request.setTags(List.of("tag1"));

        when(postDao.create(any(Post.class))).thenReturn(testPost);

        Post result = postService.createPost(request);

        assertEquals(testPost.getId(), result.getId());
        verify(postDao).create(any(Post.class));
    }

    @Test
    void updatePostThrowsWhenEntityMissing() {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle("Updated");
        request.setText("Updated content");
        request.setTags(List.of("tag1"));

        when(postDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.updatePost(999L, request));
        verify(postDao).findById(999L);
        verify(postDao, never()).update(any(Post.class));
    }

    @Test
    void incrementLikesReturnsUpdatedCounter() {
        Post likedPost = new Post();
        likedPost.setId(1L);
        likedPost.setTitle("Test Post");
        likedPost.setText("Test content");
        likedPost.setLikesCount(5);
        likedPost.setCommentsCount(0);

        when(postDao.findById(1L)).thenReturn(Optional.of(likedPost));

        int likesCount = postService.incrementLikes(1L);

        assertEquals(5, likesCount);
        verify(postDao).incrementLikes(1L);
        verify(postDao).findById(1L);
    }
}
