package gr.example.blog.service;

import gr.example.blog.dto.CategoryDto;
import gr.example.blog.model.Category;
import gr.example.blog.service.exception.ResourceNotFoundException;

import java.util.List;

public interface CategoryService {
    Category addCategory(CategoryDto categoryDto);

    Category getCategoryById(Long categoryId) throws ResourceNotFoundException;

    List<Category> getAllCategories();

    Category updateCategory(CategoryDto categoryDto) throws ResourceNotFoundException;

    void deleteCategoryById(Long categoryId) throws ResourceNotFoundException;
}
