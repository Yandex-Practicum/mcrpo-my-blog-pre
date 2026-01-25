package com.myblog.controller;

import com.myblog.dto.CreateCommentRequest;
import com.myblog.dto.UpdateCommentRequest;
import com.myblog.model.Comment;
import com.myblog.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        log.debug("GET /api/posts/{}/comments", postId);
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        
        log.debug("GET /api/posts/{}/comments/{}", postId, commentId);
        Optional<Comment> comment = commentService.getCommentById(commentId);
        return comment.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request) {
        
        log.debug("POST /api/posts/{}/comments - text: {}", postId, request.getText());
        Comment createdComment = commentService.createComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        
        // TODO: Реализовать обновление комментария
        // 1. Вызвать commentService.updateComment(commentId, request)
        // 2. Обработать исключение IllegalArgumentException -> вернуть 404
        // 3. При успехе вернуть ResponseEntity.ok(updatedComment)
        // Подсказка: посмотрите на PostController.updatePost как пример

        // 1. Валидация request
        if (request == null) {
            log.warn("PUT /api/posts/{}/comments/{} - request is null", postId, commentId);
            return ResponseEntity.badRequest().body(null);
        }

        // 2. Ручная валидация text
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            log.warn("PUT /api/posts/{}/comments/{} - text is null or empty", postId, commentId);
            return ResponseEntity.badRequest().body(null);
        }

        if (request.getText().length() > 1000) {
            log.warn("PUT /api/posts/{}/comments/{} - text too long: {}", postId, commentId, request.getText().length());
            return ResponseEntity.badRequest().body(null);
        }

        log.debug("PUT /api/posts/{}/comments/{} - text: {}", postId, commentId, request.getText());

        // 3. Обработка через try-catch
        try {
            Comment updatedComment = commentService.updateComment(commentId, request);
            return ResponseEntity.ok(updatedComment);
        } catch (IllegalArgumentException e) {
            log.error("Comment not found: {}", commentId, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating comment {}: {}", commentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        
        // TODO: Реализовать удаление комментария
        // 1. Вызвать commentService.deleteComment(commentId)
        // 2. Вернуть ResponseEntity.ok().build()
        log.debug("DELETE /api/posts/{}/comments/{}", postId, commentId);

        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Comment not found for deletion: postId={}, commentId={}", postId, commentId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting comment {}: {}", commentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

