package gr.example.blog.service;

import gr.example.blog.dto.PostDto;
import gr.example.blog.dto.PostResponse;
import gr.example.blog.model.Post;
import gr.example.blog.service.exception.ResourceNotFoundException;

import java.util.List;

public interface PostService {

    Post createPost(PostDto postDto) throws ResourceNotFoundException;

    PostResponse getAllPosts(int page, int size, String sortBy, String sortDir);

    Post getPostById(Long id) throws ResourceNotFoundException;

    Post updatePost(PostDto postDto) throws ResourceNotFoundException;

    void deletePost(Long id) throws ResourceNotFoundException;

    List<Post> getPostsByCategory(Long categoryId) throws ResourceNotFoundException;
}
