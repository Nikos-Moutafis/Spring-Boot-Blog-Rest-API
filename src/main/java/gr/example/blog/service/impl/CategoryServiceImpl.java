package gr.example.blog.service.impl;

import gr.example.blog.dto.CategoryDto;
import gr.example.blog.model.Category;
import gr.example.blog.repository.CategoryRepository;
import gr.example.blog.service.CategoryService;
import gr.example.blog.service.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final  CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public Category addCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);

        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long categoryId) throws ResourceNotFoundException {

        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) throw new ResourceNotFoundException("category","id", categoryId);

        return category.get();
    }

    @Override
    public List<Category> getAllCategories() {

        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(CategoryDto categoryDto) throws ResourceNotFoundException {

        Optional<Category> category = categoryRepository.findById(categoryDto.getId());
        if (category.isEmpty()) throw new ResourceNotFoundException("category","id", categoryDto.getId());

        Category updatedCategory = category.get();

        updatedCategory.setId(categoryDto.getId());
        updatedCategory.setName(categoryDto.getName());
        updatedCategory.setDescription(categoryDto.getDescription());

        return categoryRepository.save(updatedCategory);
    }

    @Override
    public void deleteCategoryById(Long categoryId) throws ResourceNotFoundException {

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) throw new ResourceNotFoundException("category","id", categoryId);

        categoryRepository.deleteById(categoryId);
    }


}
