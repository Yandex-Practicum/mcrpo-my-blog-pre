package com.myblog.dao;

import com.myblog.model.Comment;
import com.myblog.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentDaoIntegrationTest {

    @Autowired
    private CommentDao commentDao;

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
    void testUpdate() {
        com.myblog.model.Post post = new com.myblog.model.Post();
        post.setTitle("Test Post");
        post.setText("Test content");
        post.setTags(Arrays.asList());
        com.myblog.model.Post createdPost = postDao.create(post);
        
        Comment comment = new Comment();
        comment.setText("Original comment");
        comment.setPostId(createdPost.getId());
        Comment createdComment = commentDao.create(comment);

        createdComment.setText("Updated comment");
        Comment updatedComment = commentDao.update(createdComment);

        assertNotNull(updatedComment);
        assertEquals("Updated comment", updatedComment.getText());
    }

    @Test
    void testUpdateNotFound() {
        Comment comment = new Comment();
        comment.setId(999L);
        comment.setText("Updated comment");
        comment.setPostId(1L);

        assertThrows(IllegalArgumentException.class, () -> {
            commentDao.update(comment);
        });
    }

    @Test
    void testDelete() {

        Post post = new Post();
        post.setTitle("Test Post");
        post.setText("Test content");
        post.setTags(Arrays.asList());
        com.myblog.model.Post createdPost = postDao.create(post);
        
        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setPostId(createdPost.getId());
        Comment createdComment = commentDao.create(comment);

        commentDao.delete(createdComment.getId());

        Optional<Comment> deletedComment = commentDao.findById(createdComment.getId());
        assertFalse(deletedComment.isPresent());
    }
}
