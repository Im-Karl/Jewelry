package com.example.jewelry.product.domain;

import com.example.jewelry.product.dto.CategoryDto;
import com.example.jewelry.product.dto.CreateCategoryRequest;
import com.example.jewelry.product.web.CategoryService;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> modelMapper.map(cat, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.CATEGORY_NOT_FOUND));
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        // 1. Xử lý upload ảnh Icon lên S3
        String iconUrl = null;
        if (request.getIcon() != null && !request.getIcon().isEmpty()) {
            iconUrl = fileStorageService.storeFile(request.getIcon());
        }

        // 2. Tạo Slug (đơn giản hóa)
        String slug = request.getName().toLowerCase().replace(" ", "-");

        // 3. Tạo Entity
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(slug);
        category.setIconUrl(iconUrl); // Lưu URL S3

        // 4. Lưu DB & Return
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // Có thể thêm logic: Nếu danh mục đã có sản phẩm thì không cho xóa
        if (!categoryRepository.existsById(id)) {
            throw new DomainException(DomainExceptionCode.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }
}