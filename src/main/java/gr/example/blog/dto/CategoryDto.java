package gr.example.blog.dto;

import gr.example.blog.model.Post;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    @NotEmpty(message = "Name of category cannot be null or empty")
    @Size(min = 3, message = "Name of category body must be at least 3 characters")
    private String name;
    @NotEmpty(message = "Description cannot be null or empty")
    @Size(min = 5, message = "Description of category must be at least 5 characters")
    private String description;
}
