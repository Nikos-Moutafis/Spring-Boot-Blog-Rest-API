package gr.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    @NotEmpty(message = "Name cannot be  null or empty")
    private String name;
    @Email(message = "Email must be a valid email format")
    @NotEmpty(message = "Email  cannot be  null or empty" )
    private String email;

    @NotEmpty(message = "Body cannot be null or empty")
    @Size(min = 5, message = "Comment body must be at least 5 characters")
    private String body;
}
