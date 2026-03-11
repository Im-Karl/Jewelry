package com.example.jewelry.product.domain;

import com.example.jewelry.product.dto.*;
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
import com.example.jewelry.shared.storage.FileStorageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    private final ProductVariantRepository variantRepository;

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

    @Override
    @Transactional
    public ProductVariantDto addVariant(String productId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));

        boolean isExist = product.getVariants().stream()
                .anyMatch(v -> Objects.equals(v.getSize(), request.getSize()) &&
                        Objects.equals(v.getColor(), request.getColor()));

        if (isExist) {
            throw new RuntimeException("Biến thể với Size và Màu này đã tồn tại trong sản phẩm!");
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .size(request.getSize())
                .color(request.getColor())
                .additionalPrice(request.getAdditionalPrice() != null ? request.getAdditionalPrice() : BigDecimal.ZERO)
                .stockQuantity(request.getStockQuantity())
                .build();

        ProductVariant saveVariant = variantRepository.save(variant);
        return modelMapper.map(saveVariant, ProductVariantDto.class);
    }

    @Override
    @Transactional
    public ProductVariantDto updateVariant(String productId, UUID variantId, UpdateVariantRequest request){
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

        if(!variant.getProduct().getId().equals(productId)) {
            throw new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND);
        }

        if (request.getAdditionalPrice() != null) {
            variant.setAdditionalPrice(request.getAdditionalPrice());
        }

        if (request.getStockQuantity() != null) {
            variant.setStockQuantity(request.getStockQuantity());
        }

        ProductVariant updatedVariant = variantRepository.save(variant);
        return modelMapper.map(updatedVariant, ProductVariantDto.class);
    }

    @Override
    @Transactional
    public void deleteVariant(String productId, UUID variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

        if(!variant.getProduct().getId().equals(productId)) {throw new RuntimeException("Biến thể không thuộc sản phẩm này");}

        variantRepository.delete(variant);
    }
}