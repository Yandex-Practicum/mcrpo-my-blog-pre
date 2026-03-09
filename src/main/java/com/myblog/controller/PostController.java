package com.myblog.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myblog.dto.CreatePostRequest;
import com.myblog.dto.PostListResponse;
import com.myblog.dto.UpdatePostRequest;
import com.myblog.model.Post;
import com.myblog.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<PostListResponse> getPosts(
            @RequestParam(required = true) String search,
            @RequestParam(required = true) int pageNumber,
            @RequestParam(required = true) int pageSize) {
        
        log.debug("GET /api/posts - search: {}, pageNumber: {}, pageSize: {}", search, pageNumber, pageSize);
        PostListResponse response = postService.getPosts(search, pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        log.debug("GET /api/posts/{}", id);
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Post> getPostViaPost(@PathVariable Long id) {
        log.debug("POST /api/posts/{}", id);
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequest request) {
        log.debug("POST /api/posts - title: {}", request.getTitle());
        Post createdPost = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request) {
        
        log.debug("PUT /api/posts/{} - title: {}", id, request.getTitle());        
        
        Post updatedPost = postService.updatePost(id, request);
        return ResponseEntity.ok(updatedPost);        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {        
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> incrementLikes(@PathVariable Long id) {
        log.debug("POST /api/posts/{}/likes", id);
        
        int likesCount = postService.incrementLikes(id);
        return ResponseEntity.ok(likesCount);
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Integer> removeLike(@PathVariable Long id) {    
        log.debug("DELETE /api/posts/{}/likes", id);        

        int likesCount = postService.decrementLikes(id);
        return ResponseEntity.ok(likesCount);       
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Void> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {
        
        log.debug("PUT /api/posts/{}/image - filename: {}", id, image.getOriginalFilename());
        
        try {
            byte[] imageData = image.getBytes();
            String contentType = image.getContentType();
            postService.saveImage(id, imageData, contentType);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Error saving image for post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        log.debug("GET /api/posts/{}/image", id);
        
        Optional<byte[]> imageData = postService.getImage(id);
        if (imageData.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<String> contentType = postService.getImageContentType(id);
        MediaType mediaType = contentType
            .map(MediaType::parseMediaType)
            .orElse(MediaType.IMAGE_JPEG);
        
        return ResponseEntity.ok()
            .contentType(mediaType)
            .body(imageData.get());
    }
}

