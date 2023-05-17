package gr.example.blog.rest;

import gr.example.blog.dto.CommentDto;
import gr.example.blog.model.Comment;
import gr.example.blog.service.CommentService;
import gr.example.blog.service.exception.BlogAPIException;
import gr.example.blog.service.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final ModelMapper mapper;


    @Autowired
    public CommentController(CommentService commentService, ModelMapper mapper) {
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @RequestMapping(path = "/posts/{postId}/comments", method = RequestMethod.POST)
    public ResponseEntity<CommentDto> createComment(@PathVariable("postId") Long postId,
                                                    @Valid  @RequestBody CommentDto commentDto,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Comment comment = commentService.createComment(postId, commentDto);

            CommentDto createdDto = map(comment);

            return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
        }catch (ResourceNotFoundException ex){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path ="/posts/{postId}/comments" ,method = RequestMethod.GET)
    public ResponseEntity<List<CommentDto>> getCommentsByPostId (@PathVariable("postId") Long postId) {
        List<Comment>  comments = commentService.getCommentsByPostId(postId);
        if (comments.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(new CommentDto(comment.getId(),comment.getName(),
                    comment.getEmail(),comment.getBody()));
        }

        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }


    //Get a comment by id belonging to a post
    @RequestMapping(path = "/posts/{postId}/comments/{commentId}",method = RequestMethod.GET)
    public ResponseEntity<CommentDto> getCommentById(@PathVariable("postId") Long postId,
                                                     @PathVariable("commentId") Long commentId) {
        try {
            Comment comment = commentService.getCommentById(postId, commentId);

            CommentDto commentDto = map(comment);

            return new ResponseEntity<>(commentDto, HttpStatus.OK);
        }catch (ResourceNotFoundException exception){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (BlogAPIException blogAPIException) {
            return  new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @RequestMapping(path = "/posts/{postId}/comments/{commentId}",method = RequestMethod.PUT)
    public ResponseEntity<CommentDto> updateComment(@PathVariable("postId") Long postId,
                                                    @PathVariable("commentId") Long commentId,
                                                    @Valid  @RequestBody CommentDto dto,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Comment comment = commentService.updateComment(postId, commentId, dto);
            CommentDto updatedDto = map(comment);

            return new ResponseEntity<>(updatedDto, HttpStatus.OK);
        }catch (ResourceNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (BlogAPIException blogAPIException) {
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @RequestMapping(path = "/posts/{postId}/comments/{commentId}", method = RequestMethod.DELETE)
    public ResponseEntity<CommentDto> deleteComment(@PathVariable("postId") Long postId,
                                                    @PathVariable("commentId") Long commentId){

        try {
            Comment comment = commentService.getCommentById(postId, commentId);
            commentService.deleteComment(postId, commentId);
            CommentDto deletedDTO = map(comment);

            return new ResponseEntity<>(deletedDTO, HttpStatus.OK);

        }catch (ResourceNotFoundException exception) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (BlogAPIException blogAPIException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private CommentDto map(Comment comment) {
        CommentDto commentDto = mapper.map(comment, CommentDto.class);

        return commentDto;
    }
}
