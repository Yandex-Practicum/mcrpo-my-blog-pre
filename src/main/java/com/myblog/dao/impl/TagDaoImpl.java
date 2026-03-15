package com.myblog.dao.impl;

import com.myblog.dao.TagDao;
import com.myblog.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TagDaoImpl implements TagDao {

    private static final Logger log = LoggerFactory.getLogger(TagDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * ИСПРАВЛЕННЫЙ МЕТОД: создаёт тег с повторной проверкой
     */
    @Override
    public Tag create(String tagName) {
        // Двойная проверка на существование (на случай гонки)
        Optional<Tag> existingTag = findByName(tagName);
        if (existingTag.isPresent()) {
            log.debug("Tag already exists: {}, returning existing", tagName);
            return existingTag.get();
        }
        
        String sql = "INSERT INTO tags (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, tagName);
                return ps;
            }, keyHolder);

            Long tagId = keyHolder.getKey().longValue();
            Tag tag = new Tag();
            tag.setId(tagId);
            tag.setName(tagName);
            log.debug("Created new tag: {} with id: {}", tagName, tagId);
            return tag;
            
        } catch (DuplicateKeyException e) {
            // Если всё же произошла ошибка дубликата, пробуем найти существующий
            log.debug("Duplicate key exception while creating tag: {}, trying to find existing", tagName);
            Optional<Tag> existing = findByName(tagName);
            if (existing.isPresent()) {
                log.debug("Found existing tag after duplicate key: {} with id: {}", tagName, existing.get().getId());
                return existing.get();
            }
            log.error("Failed to handle duplicate key for tag: {}", tagName);
            throw e;
        }
    }

    @Override
    public Optional<Tag> findByName(String name) {
        String sql = "SELECT id, name FROM tags WHERE name = ?";
        try {
            Tag tag = jdbcTemplate.queryForObject(sql, new TagRowMapper(), name);
            return Optional.ofNullable(tag);
        } catch (Exception e) {
            log.debug("Tag not found with name: {}", name);
            return Optional.empty();
        }
    }

    @Override
    public List<Tag> findByPostId(Long postId) {
        String sql = "SELECT t.id, t.name FROM tags t " +
                     "JOIN post_tags pt ON t.id = pt.tag_id " +
                     "WHERE pt.post_id = ?";
        return jdbcTemplate.query(sql, new TagRowMapper(), postId);
    }

    /**
     * ИСПРАВЛЕННЫЙ МЕТОД: проверяет существование связи перед созданием
     */
    @Override
    public void linkTagToPost(Long tagId, Long postId) {
        // Проверим, не существует ли уже такая связь
        String checkSql = "SELECT COUNT(*) FROM post_tags WHERE post_id = ? AND tag_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, postId, tagId);
        
        if (count == 0) {
            String sql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, postId, tagId);
            log.debug("Linked tag {} to post {}", tagId, postId);
        } else {
            log.debug("Tag {} already linked to post {}, skipping", tagId, postId);
        }
    }

    @Override
    public void unlinkAllTagsFromPost(Long postId) {
        String sql = "DELETE FROM post_tags WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
        log.debug("Unlinked all tags from post {}", postId);
    }

    private static class TagRowMapper implements RowMapper<Tag> {
        @Override
        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tag tag = new Tag();
            tag.setId(rs.getLong("id"));
            tag.setName(rs.getString("name"));
            return tag;
        }
    }
}
