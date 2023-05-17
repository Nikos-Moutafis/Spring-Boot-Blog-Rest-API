package gr.example.blog.service.impl;

import gr.example.blog.dto.CommentDto;
import gr.example.blog.model.Comment;
import gr.example.blog.model.Post;
import gr.example.blog.repository.CommentRepository;
import gr.example.blog.repository.PostRepository;
import gr.example.blog.service.CommentService;
import gr.example.blog.service.exception.BlogAPIException;
import gr.example.blog.service.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private ModelMapper mapper;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
                              ModelMapper mapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Comment createComment(Long postId, CommentDto commentDto) throws ResourceNotFoundException {
        Comment comment = mapToComment(commentDto);

        //retrieve post entity by id
        Post post = getPostById(postId);

        //set post to comment entity
        comment.setPost(post);

        //save entity to database
        return  commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        //retrieve comments by postId

        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments;
    }

    @Override
    public Comment getCommentById(Long postId, Long commentId) throws ResourceNotFoundException, BlogAPIException {
        //retrieve post entity by id
        Post currentPost = getPostById(postId);


        //retrieve comment by id
        Comment currectComment = getCommentById(commentId);

        if (!currectComment.getPost().getId().equals(currentPost.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        return currectComment;
    }


    @Override
    @Transactional
    public Comment updateComment(Long postId, Long commentId, CommentDto commentDto)
            throws ResourceNotFoundException, BlogAPIException{
        //retrieve post entity by id
        Post currentPost = getPostById(postId);


        //retrieve comment by id
        Comment currectComment = getCommentById(commentId);

        if (!currectComment.getPost().getId().equals(currentPost.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        currectComment.setName(commentDto.getName());
        currectComment.setBody(commentDto.getBody());
        currectComment.setEmail(commentDto.getEmail());

        return commentRepository.save(currectComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) throws ResourceNotFoundException, BlogAPIException {
        //retrieve post entity by id
        Post currentPost = getPostById(postId);


        //retrieve comment by id
        Comment currectComment = getCommentById(commentId);

        if (!currectComment.getPost().getId().equals(currentPost.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        commentRepository.delete(currectComment);
    }


    private Post getPostById(Long postId) throws ResourceNotFoundException {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) throw new ResourceNotFoundException("Post", "ID", postId);
        return post.get();
    }


    private Comment getCommentById(Long commentId) throws ResourceNotFoundException {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) throw new ResourceNotFoundException("Comment", "ID", commentId);
        return comment.get();
    }


    private Comment mapToComment(CommentDto commentDto) {
        Comment comment = mapper.map(commentDto, Comment.class);

        return comment;
    }
}
