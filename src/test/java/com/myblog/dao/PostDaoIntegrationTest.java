package com.myblog.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import com.myblog.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.myblog.MyBlogApplication;

@SpringBootTest(classes = MyBlogApplication.class)
@Transactional
class PostDaoIntegrationTest {

    @Autowired
    private PostDao postDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM post_images");
        jdbcTemplate.execute("DELETE FROM post_tags");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM tags");
        jdbcTemplate.execute("DELETE FROM posts");
    }

    @Test
    void createAndFindPost() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setText("Test content");
        post.setTags(List.of("tag1", "tag2"));

        Post createdPost = postDao.create(post);

        assertNotNull(createdPost.getId());
        assertEquals("Test Post", createdPost.getTitle());
        assertEquals(0, createdPost.getLikesCount());

        Optional<Post> foundPost = postDao.findById(createdPost.getId());
        assertTrue(foundPost.isPresent());
        assertEquals(2, foundPost.get().getTags().size());
    }

    @Test
    void searchPostsByTag() {
        Post post1 = new Post();
        post1.setTitle("Post 1");
        post1.setText("Content");
        post1.setTags(List.of("java", "spring"));
        postDao.create(post1);

        Post post2 = new Post();
        post2.setTitle("Post 2");
        post2.setText("Content");
        post2.setTags(List.of("python"));
        postDao.create(post2);

        List<Post> posts = postDao.findAll("#java", 1, 10);

        assertEquals(1, posts.size());
        assertEquals("Post 1", posts.get(0).getTitle());
    }

    @Test
    void deletePostRemovesEntity() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setText("Test content");
        post.setTags(List.of());
        Post createdPost = postDao.create(post);

        postDao.delete(createdPost.getId());

        Optional<Post> foundPost = postDao.findById(createdPost.getId());
        assertFalse(foundPost.isPresent());
    }
}
