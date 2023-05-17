package gr.example.blog.dto;

import gr.example.blog.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private List<PostDto> content;
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;
    private boolean last;
}
