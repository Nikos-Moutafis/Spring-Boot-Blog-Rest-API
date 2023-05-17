package gr.example.blog.rest;

import gr.example.blog.dto.CommentDto;
import gr.example.blog.model.Comment;
import gr.example.blog.service.CommentService;
import gr.example.blog.service.exception.BlogAPIException;
import gr.example.blog.service.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    @Operation( summary = "Create a comment for a certain post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "comment created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input was supplied",
                    content = @Content),
    @ApiResponse(responseCode = "404", description = "Post not found for creating the comment",
            content = @Content)})

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


    @Operation(summary = "Get a all comments for a certain post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments Found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Comments not found",
                    content = @Content)})
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
    @Operation(summary = "Get a comment belonging to a certain post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class)) }),
            @ApiResponse(responseCode = "400", description = "The requested comment does not belong to the selected post ",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content) })
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
    @Operation(summary = "Update a Comment of a certain post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input was supplied/ Comment does not belong to selected post",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content) })
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
    @Operation(summary = "Delete a Comment of a certain post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment delete",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Comment does not belong to selected post",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content) })
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
