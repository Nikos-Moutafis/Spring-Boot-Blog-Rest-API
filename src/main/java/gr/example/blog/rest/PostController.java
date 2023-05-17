package gr.example.blog.rest;

import gr.example.blog.dto.PostDto;
import gr.example.blog.dto.PostResponse;
import gr.example.blog.model.Post;
import gr.example.blog.service.PostService;
import gr.example.blog.service.exception.ResourceNotFoundException;
import gr.example.blog.service.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(
        name = "API For Post Resource"
)
public class PostController {

    private final PostService postService;

    private final ModelMapper mapper;

    @Autowired
    public PostController(PostService postService, ModelMapper mapper){
        this.postService = postService;
        this.mapper = mapper;
    }

    @Operation( summary = "Create a post")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input was supplied",
                    content = @Content)})
    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto,
                                              BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Post post = postService.createPost(postDto);

            PostDto createdDto = map(post);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdDto.getId())
                    .toUri();

            return ResponseEntity.created(location).body(createdDto);
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Operation(
            summary = "Get all posts",
            description = "Get all posts, choose page or size of response and sort by field" +
                    "  or sort by ascending or descending order (are all optional)" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts Found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)) })})
    @RequestMapping(value = "/posts", method = RequestMethod.GET)
    public PostResponse getAllPost(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size,
            @RequestParam(value = "sortBy",defaultValue = AppConstants.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
            String sortDir ){

        return postService.getAllPosts(page, size, sortBy, sortDir);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Get a Post by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post Found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content)})
    @RequestMapping(value = "/posts/{postId}", method = RequestMethod.GET)
    public ResponseEntity<PostDto> getPostById(@PathVariable("postId") Long theId){
        try {
            Post post = postService.getPostById(theId);
            PostDto postDto = map(post);

            return new ResponseEntity<>(postDto, HttpStatus.OK);
        }catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Update a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input was supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content) })
    @RequestMapping(value = "/posts/{postId}", method = RequestMethod.PUT)
    public ResponseEntity<PostDto> updatePost(@Valid @RequestBody PostDto postDto,
                                              @PathVariable("postId") Long theId,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            postDto.setId(theId);
            Post post = postService.updatePost(postDto);
            PostDto postUpdatedDto = map(post);
            return new ResponseEntity<>(postUpdatedDto, HttpStatus.OK);
        }catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Delete a Post by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post Deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content)})
    @RequestMapping(value = "/posts/{postId}", method = RequestMethod.DELETE)
    public ResponseEntity<PostDto> deletePost(@PathVariable("postId") Long theId) {
        try {
            Post post = postService.getPostById(theId);
            postService.deletePost(theId);
            PostDto postDto = map(post);
            return new ResponseEntity<>(postDto, HttpStatus.OK);
        }catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get Posts by their category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts Found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Posts not found",
                    content = @Content)})
    @RequestMapping(value = "/categories/{categoryId}/posts", method = RequestMethod.GET)
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            List<Post> posts = postService.getPostsByCategory(categoryId);
            List<PostDto> postDtos = posts.stream().map((post) -> map(post))
                    .toList();

            return new ResponseEntity<>(postDtos,HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private PostDto map(Post post){
        PostDto postDto = mapper.map(post, PostDto.class);

        return postDto;
    }
}
