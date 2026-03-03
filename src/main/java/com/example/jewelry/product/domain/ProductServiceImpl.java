package com.example.jewelry.product.domain;

import com.example.jewelry.product.dto.ProductDto;
import com.example.jewelry.product.web.ProductService;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.example.jewelry.product.dto.CreateProductRequest;
import com.example.jewelry.shared.storage.FileStorageService;
import com.example.jewelry.product.domain.Category;
import com.example.jewelry.product.domain.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> getProductsByFengShui(String element) {
        return productRepository.findByFengShuiElement(element).stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto createProduct(CreateProductRequest request) {
        // 1. Tìm Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.CATEGORY_NOT_FOUND));

        // 2. Upload ảnh (nếu có)
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = fileStorageService.storeFile(request.getImage());
        }

        // 3. Tạo Entity
        Product product = Product.builder()
                .name(request.getName())
                .slug(request.getName().toLowerCase().replace(" ", "-")) // Tạo slug đơn giản
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .materialType(request.getMaterialType())
                .stoneType(request.getStoneType())
                .stockQuantity(request.getStockQuantity())
                .platingColor(request.getPlatingColor())
                .fengShuiElement(request.getFengShuiElement())
                .mainImageUrl(imageUrl)
                .category(category)
                .isArEnabled(false)
                .build();

        // 4. Lưu và trả về
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }
}