package com.myblog.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.myblog.dao.PostDao;
import com.myblog.dao.TagDao;
import com.myblog.model.Post;
import com.myblog.model.Tag;

@Repository
public class PostDaoImpl implements PostDao {

    private static final Logger log = LoggerFactory.getLogger(PostDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final TagDao tagDao;

    public PostDaoImpl(JdbcTemplate jdbcTemplate, TagDao tagDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagDao = tagDao;
    }

    @Override
    public Post create(Post post) {
        String sql = "INSERT INTO posts (title, text, likes_count) VALUES (?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            return ps;
        }, keyHolder);

        Long postId = keyHolder.getKey().longValue();
        post.setId(postId);
        post.setLikesCount(0);
        post.setCommentsCount(0);

        if (post.getTags() != null && !post.getTags().isEmpty()) {
            saveTags(postId, post.getTags());
        }

        return findById(postId).orElse(post);
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "SELECT p.id, p.title, p.text, p.likes_count, p.created_at, p.updated_at, "
                + "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as comments_count "
                + "FROM posts p WHERE p.id = ?";

        try {
            Post post = jdbcTemplate.queryForObject(sql, new PostRowMapper(), id);
            if (post != null) {
                post.setTags(tagDao.findByPostId(id).stream().map(Tag::getName).toList());
            }
            return Optional.ofNullable(post);
        } catch (Exception exception) {
            log.debug("Post not found with id: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Post> findAll(String search, int pageNumber, int pageSize) {
        List<String> tags = new ArrayList<>();
        String titleSearch = parseSearchQuery(search, tags);

        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.title, p.text, p.likes_count, p.created_at, p.updated_at, "
                        + "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as comments_count "
                        + "FROM posts p WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (titleSearch != null && !titleSearch.isEmpty()) {
            sql.append(" AND LOWER(p.title) LIKE LOWER(?)");
            params.add("%" + titleSearch + "%");
        }

        if (!tags.isEmpty()) {
            for (String tag : tags) {
                sql.append(" AND EXISTS (SELECT 1 FROM post_tags pt "
                        + "JOIN tags t ON pt.tag_id = t.id "
                        + "WHERE pt.post_id = p.id AND t.name = ?)");
                params.add(tag);
            }
        }

        sql.append(" ORDER BY p.created_at DESC");
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((pageNumber - 1) * pageSize);

        List<Post> posts = jdbcTemplate.query(sql.toString(), new PostRowMapper(), params.toArray());

        for (Post post : posts) {
            post.setTags(tagDao.findByPostId(post.getId()).stream().map(Tag::getName).toList());
            if (post.getText().length() > 128) {
                post.setText(post.getText().substring(0, 128) + "...");
            }
        }

        return posts;
    }

    @Override
    public Post update(Post post) {
        String sql = "UPDATE posts SET title = ?, text = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getText(), post.getId());

        tagDao.unlinkAllTagsFromPost(post.getId());
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            saveTags(post.getId(), post.getTags());
        }

        return findById(post.getId()).orElse(post);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM comments WHERE post_id = ?", id);
        jdbcTemplate.update("DELETE FROM post_tags WHERE post_id = ?", id);
        jdbcTemplate.update("DELETE FROM post_images WHERE post_id = ?", id);

        int deletedId = jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
        if (deletedId == 0) {
            throw new IllegalArgumentException("No post with id " + id + " found");
        }
    }

    @Override
    public void incrementLikes(Long id) {
        jdbcTemplate.update("UPDATE posts SET likes_count = likes_count + 1 WHERE id = ?", id);
    }

    @Override
    public void decrementLikes(Long id) {
        jdbcTemplate.update("UPDATE posts SET likes_count = GREATEST(likes_count - 1, 0) WHERE id = ?", id);
    }

    @Override
    public int getTotalCount(String search) {
        List<String> tags = new ArrayList<>();
        String titleSearch = parseSearchQuery(search, tags);

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM posts p WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (titleSearch != null && !titleSearch.isEmpty()) {
            sql.append(" AND LOWER(p.title) LIKE LOWER(?)");
            params.add("%" + titleSearch + "%");
        }

        if (!tags.isEmpty()) {
            for (String tag : tags) {
                sql.append(" AND EXISTS (SELECT 1 FROM post_tags pt "
                        + "JOIN tags t ON pt.tag_id = t.id "
                        + "WHERE pt.post_id = p.id AND t.name = ?)");
                params.add(tag);
            }
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return count != null ? count : 0;
    }

    @Override
    public void saveImage(Long postId, byte[] imageData, String contentType) {
        jdbcTemplate.update("DELETE FROM post_images WHERE post_id = ?", postId);
        jdbcTemplate.update("INSERT INTO post_images (post_id, image_data, content_type) VALUES (?, ?, ?)",
                postId, imageData, contentType);
    }

    @Override
    public Optional<byte[]> getImage(Long postId) {
        try {
            byte[] imageData = jdbcTemplate.queryForObject(
                    "SELECT image_data FROM post_images WHERE post_id = ?",
                    byte[].class,
                    postId);
            return Optional.ofNullable(imageData);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getImageContentType(Long postId) {
        try {
            String contentType = jdbcTemplate.queryForObject(
                    "SELECT content_type FROM post_images WHERE post_id = ?",
                    String.class,
                    postId);
            return Optional.ofNullable(contentType);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private void saveTags(Long postId, List<String> tagNames) {
        for (String tagName : tagNames) {
            if (tagName == null || tagName.trim().isEmpty()) {
                continue;
            }

            String cleanTagName = tagName.startsWith("#") ? tagName.substring(1) : tagName;
            Optional<Tag> existingTag = tagDao.findByName(cleanTagName);
            Long tagId;
            if (existingTag.isPresent()) {
                tagId = existingTag.get().getId();
            } else {
                Tag newTag = tagDao.create(cleanTagName);
                tagId = newTag.getId();
            }

            tagDao.linkTagToPost(tagId, postId);
        }
    }

    private String parseSearchQuery(String search, List<String> tags) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }

        String[] words = search.trim().split("\\s+");
        StringBuilder titleSearch = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (word.startsWith("#")) {
                String tagName = word.substring(1);
                if (!tagName.isEmpty()) {
                    tags.add(tagName);
                }
            } else {
                if (titleSearch.length() > 0) {
                    titleSearch.append(" ");
                }
                titleSearch.append(word);
            }
        }

        return titleSearch.toString();
    }

    private static class PostRowMapper implements RowMapper<Post> {
        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            Post post = new Post();
            post.setId(rs.getLong("id"));
            post.setTitle(rs.getString("title"));
            post.setText(rs.getString("text"));
            post.setLikesCount(rs.getInt("likes_count"));
            post.setCommentsCount(rs.getInt("comments_count"));
            post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return post;
        }
    }
}
