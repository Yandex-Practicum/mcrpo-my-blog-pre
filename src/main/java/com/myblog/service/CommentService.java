package com.myblog.service.impl;

import com.myblog.dao.CommentDao;
import com.myblog.model.Comment;
import com.myblog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service  
public class CommentServiceImpl implements CommentService {
	
    @Autowired  
    private CommentDao commentDao;
}

@Override  
public Comment updateComment(Long commentId, UpdateCommentRequest request) {
    // обновление комментария  
}

@Override  
public void deleteComment(Long commentId) {
    // удаление комментария  
}
}
