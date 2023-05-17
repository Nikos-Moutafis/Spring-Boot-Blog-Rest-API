package gr.example.blog.service;

import gr.example.blog.dto.CommentDto;
import gr.example.blog.model.Comment;
import gr.example.blog.service.exception.BlogAPIException;
import gr.example.blog.service.exception.ResourceNotFoundException;

import java.util.List;

public interface CommentService {

    Comment createComment(Long postId, CommentDto commentDto) throws ResourceNotFoundException;

    List<Comment> getCommentsByPostId(Long postId);

    Comment getCommentById(Long postId, Long commentId) throws ResourceNotFoundException, BlogAPIException;

    Comment updateComment(Long postId, Long commentId, CommentDto commentDto)
            throws ResourceNotFoundException,BlogAPIException;

    void deleteComment(Long postId, Long commentId) throws ResourceNotFoundException, BlogAPIException;
}
