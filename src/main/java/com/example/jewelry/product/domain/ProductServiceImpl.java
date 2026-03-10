package com.example.jewelry.product.domain;

import com.example.jewelry.product.dto.ProductDto;
import com.example.jewelry.product.dto.UpdateProductRequest;
import com.example.jewelry.product.web.ProductService;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.jewelry.product.dto.CreateProductRequest;
import com.example.jewelry.shared.storage.FileStorageService;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

    @Override
    public PageResponse<ProductDto> getProductsWithFilter(String search, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, int page, int size, String sortBy, String sortDir) {
        String finalSearch = (search == null) ? "" : search;
        Long finalCategoryId = (categoryId == null) ? 0L : categoryId; // Dùng 0L làm số giả vì ID DB luôn bắt đầu từ 1
        BigDecimal finalMinPrice = (minPrice == null) ? BigDecimal.ZERO : minPrice;
        BigDecimal finalMaxPrice = (maxPrice == null) ? new BigDecimal("999999999") : maxPrice; // 999 triệu

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.filterProducts(finalSearch, finalCategoryId, finalMinPrice, finalMaxPrice, pageable);

        List<ProductDto> content = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());

        return PageResponse.<ProductDto>builder()
                .content(content)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .isLast(productPage.isLast())
                .build();
    }

    @Override
    public ProductDto updateProduct(String id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));

        if (StringUtils.hasText(request.getName())) product.setName(request.getName());
        if (StringUtils.hasText(request.getDescription())) product.setDescription(request.getDescription());
        if (request.getBasePrice() != null) product.setBasePrice(request.getBasePrice());
        if (StringUtils.hasText(request.getMaterialType())) product.setMaterialType(request.getMaterialType());
        if (StringUtils.hasText(request.getStoneType())) product.setStoneType(request.getStoneType());
        if (StringUtils.hasText(request.getPlatingColor())) product.setPlatingColor(request.getPlatingColor());
        if (StringUtils.hasText(request.getFengShuiElement())) product.setFengShuiElement(request.getFengShuiElement());


        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new DomainException(DomainExceptionCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }

        if (request.getNewImage() != null && !request.getNewImage().isEmpty()) {
            String newImageUrl = fileStorageService.storeFile(request.getNewImage());
            product.setMainImageUrl(newImageUrl);
        }

        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    public void toggleProductStatus(String id, boolean isDeleted) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));

        product.setDeleted(isDeleted);
        productRepository.save(product);
    }
}