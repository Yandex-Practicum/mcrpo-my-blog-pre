package com.myblog.service.impl;

import com.myblog.dao.CommentDao;
import com.myblog.dto.CreateCommentRequest;
import com.myblog.dto.UpdateCommentRequest;
import com.myblog.model.Comment;
import com.myblog.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    private final CommentDao commentDao;

    public CommentServiceImpl(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        log.debug("Getting comments for post with id: {}", postId);
        return commentDao.findByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long commentId) {
        log.debug("Getting comment by id: {}", commentId);
        return commentDao.findById(commentId);
    }

    @Override
    @Transactional
    public Comment createComment(CreateCommentRequest request) {
        log.debug("Creating new comment for post with id: {}", request.getPostId());
        
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setPostId(request.getPostId());
        
        return commentDao.create(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, UpdateCommentRequest request) {
        // TODO: Реализовать обновление комментария
        // 1. Проверить существование комментария через commentDao.findById(commentId)
        // 2. Если комментарий не найден - выбросить IllegalArgumentException
        // 3. Обновить текст комментария: comment.setText(request.getText())
        // 4. Вызвать commentDao.update(comment)
        // 5. Вернуть обновлённый комментарий
        log.debug("Updating comment with id: {}", commentId);

        Optional<Comment> existingComment = commentDao.findById(commentId);
        if (existingComment.isEmpty()) {
            throw new IllegalArgumentException("Comment not found with id: " + commentId);
        }

        Comment comment = existingComment.get();
        comment.setText(request.getText());

        return commentDao.update(comment);
    }

    // Вспомогательный метод для валидации текста комментария
    private void validateCommentText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Текст комментария не может быть null");
        }

        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new IllegalArgumentException("Текст комментария не может быть пустым");
        }

        if (trimmedText.length() > 1000) { // Максимальная длина комментария
            throw new IllegalArgumentException("Текст комментария не может превышать 1000 символов");
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        // TODO: Реализовать удаление комментария
        // 1. Вызвать commentDao.delete(commentId)
        // Подсказка: посмотрите на метод createComment как пример
        log.debug("Deleting comment with id: {}", commentId);
        commentDao.delete(commentId);
    }
}

