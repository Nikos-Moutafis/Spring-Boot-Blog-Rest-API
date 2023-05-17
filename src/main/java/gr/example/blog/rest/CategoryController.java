package gr.example.blog.rest;

import gr.example.blog.dto.CategoryDto;
import gr.example.blog.model.Category;
import gr.example.blog.service.CategoryService;
import gr.example.blog.service.exception.ResourceNotFoundException;
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
public class CategoryController {

    private final CategoryService categoryService;

    private final ModelMapper mapper;

    @Autowired
    public CategoryController(CategoryService categoryService, ModelMapper mapper) {
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @RequestMapping(path = "/categories", method = RequestMethod.POST)
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.addCategory(categoryDto);
        CategoryDto addedDto = mapToDto(category);

        return new ResponseEntity<>(addedDto, HttpStatus.CREATED);
    }


    @RequestMapping(path = "/categories/{categoryId}", method = RequestMethod.GET)
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("categoryId") Long categoryId) {

        try{
            Category category = categoryService.getCategoryById(categoryId);

            CategoryDto categoryDto = mapToDto(category);

            return new ResponseEntity<>(categoryDto, HttpStatus.OK);
        }catch (ResourceNotFoundException exc) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/categories", method = RequestMethod.GET)
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDto> categoryDtoList = new ArrayList<>();

        for (Category category : categories) {
            categoryDtoList.add(new CategoryDto(category.getId(), category.getName(), category.getDescription()));
        }

        return new ResponseEntity<>(categoryDtoList, HttpStatus.OK);
    }


    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.PUT)
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                                      @PathVariable("categoryId") Long categoryId,
                                                      BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            categoryDto.setId(categoryId);
            Category category = categoryService.updateCategory(categoryDto);
            CategoryDto updatedDto = map(category);
            return new ResponseEntity<>(updatedDto, HttpStatus.OK);
        }catch (ResourceNotFoundException ex){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable("categoryId") Long categoryId) {

        try {
            Category category = categoryService.getCategoryById(categoryId);
            categoryService.deleteCategoryById(categoryId);
            CategoryDto dto = map(category);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch (ResourceNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private CategoryDto mapToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        categoryDto.setId(category.getId());
        return categoryDto;
    }

    private CategoryDto map(Category category){
        CategoryDto categoryDto = mapper.map(category, CategoryDto.class);

        return categoryDto;
    }

}
