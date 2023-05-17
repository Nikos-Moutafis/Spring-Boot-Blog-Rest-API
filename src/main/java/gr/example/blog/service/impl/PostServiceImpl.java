package gr.example.blog.service.impl;

import gr.example.blog.dto.PostDto;
import gr.example.blog.dto.PostResponse;
import gr.example.blog.model.Category;
import gr.example.blog.model.Post;
import gr.example.blog.repository.CategoryRepository;
import gr.example.blog.repository.PostRepository;
import gr.example.blog.service.PostService;
import gr.example.blog.service.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper mapper;

    @Autowired
    public  PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, ModelMapper mapper){
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Post createPost(PostDto postDto) throws ResourceNotFoundException {

        Optional<Category> categoryOpt = categoryRepository.findById(postDto.getCategoryId());
        if (categoryOpt.isEmpty()) throw new ResourceNotFoundException("Category", "ID", postDto.getCategoryId());
        Category category = categoryOpt.get();

        Post  post = mapToPost(postDto);
        post.setCategory(category);
        return  postRepository.save(post);
    }


    @Override
    public PostResponse getAllPosts(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //create pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        //get content for page object
        List<Post> listOfPosts = posts.getContent();

        List<PostDto> content= listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPage(posts.getNumber());
        postResponse.setSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public Post getPostById(Long id) throws ResourceNotFoundException {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) throw new ResourceNotFoundException("Post", "Id", id);
        return post.get();
    }

    @Override
    @Transactional
    public Post updatePost(PostDto postDto) throws ResourceNotFoundException {

        Optional<Post> postOpt = postRepository.findById(postDto.getId());
        if (postOpt.isEmpty()) throw new ResourceNotFoundException("Post", "Id", postDto.getId());
        Post post = postOpt.get();

        Optional<Category> categoryOpt = categoryRepository.findById(postDto.getCategoryId());
        if (categoryOpt.isEmpty()) throw new ResourceNotFoundException("Category", "ID", postDto.getCategoryId());
        Category category = categoryOpt.get();

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        post.setCategory(category);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id) throws ResourceNotFoundException {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) throw new ResourceNotFoundException("Post", "Id", id);
        postRepository.deleteById(id);
    }

    @Override
    public List<Post> getPostsByCategory(Long categoryId) throws ResourceNotFoundException {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) throw new ResourceNotFoundException("Category", "ID", categoryId);

        List<Post> posts = postRepository.findPostByCategoryId(categoryId);
        return posts;
    }


    private Post mapToPost(PostDto postDto) {
        Post post = mapper.map(postDto, Post.class);

        return post;
    }

    private PostDto mapToDTO(Post post){
        PostDto postDto = mapper.map(post, PostDto.class);
//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setTitle(post.getTitle());
//        postDto.setDescription(post.getDescription());
//        postDto.setContent(post.getContent());
        return postDto;
    }
}
