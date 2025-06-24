package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.ProductDTO;
import com.miniproject.rookiejangter.entity.*;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final DibsRepository dibsRepository;

    /**
     * 상품을 생성합니다.
     *
     * @param requestDto 상품 생성 요청 DTO
     * @param userId     사용자 ID
     * @return 생성된 상품 정보 DTO
     */
    @Transactional
    public ProductDTO.Response createProduct(ProductDTO.Request requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, requestDto.getCategoryId()));

        Product product = Product.builder()
                .category(category)
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .price(requestDto.getPrice())
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        Product savedProduct = productRepository.save(product);

        return mapToProductDTOResponse(savedProduct, userId);
    }

    /**
     * 상품 ID로 상품을 조회합니다.
     *
     * @param productId      상품 ID
     * @param currentUserId  현재 사용자 ID (조회 시 사용)
     * @return 조회된 상품 정보 DTO
     */
    @Transactional
    public ProductDTO.Response getProductById(Long productId, Long currentUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        product.incrementViewCount();
        productRepository.save(product);

        List<Image> images = imageRepository.findByProduct_ProductId(productId);
        return mapToProductDTOResponse(product, currentUserId);
    }

    /**
     * 상품 정보를 업데이트합니다.
     *
     * @param productId 상품 ID
     * @param requestDto 상품 업데이트 요청 DTO
     * @param userId    사용자 ID (업데이트 권한 확인용)
     * @return 업데이트된 상품 정보 DTO
     */
    @Transactional
    public ProductDTO.Response updateProduct(Long productId, ProductDTO.UpdateRequest requestDto, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        Category newCategory = null;
        if (requestDto.getCategoryId() != null) {
            newCategory = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, requestDto.getCategoryId()));
        } else {
            newCategory = product.getCategory(); // 카테고리가 업데이트되지 않으면 기존 카테고리 유지
        }

        // 엔티티의 비즈니스 메서드를 호출하여 상태를 변경합니다.
        product.updateProductInfo(
                newCategory,
                requestDto.getTitle() != null ? requestDto.getTitle() : product.getTitle(),
                requestDto.getContent() != null ? requestDto.getContent() : product.getContent(),
                requestDto.getPrice() != null ? requestDto.getPrice() : product.getPrice()
        );
        // Product updatedProduct = productRepository.save(product); // 필요에 따라 호출

        return mapToProductDTOResponse(product, userId);
    }

    /**
     * 상품을 삭제합니다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID (삭제 권한 확인용)
     */
    @Transactional
    public void deleteProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        if (!product.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PRODUCT_OPERATION_FORBIDDEN, "삭제");
        }

        if (product.getIsReserved().equals(true) || product.getIsCompleted().equals(true)) {
            throw new BusinessException(ErrorCode.RESERVATION_REMAIN_CANNOT_DELETE);
        }

        imageRepository.deleteAll(imageRepository.findByProduct_ProductId(productId));
        dibsRepository.deleteAll(dibsRepository.findByProduct_ProductId(productId));

        productRepository.delete(product);
    }

    /**
     * 특정 사용자의 상품 목록을 페이지네이션하여 조회합니다.
     *
     * @param targetUserId  대상 사용자 ID
     * @param pageable      페이지네이션 정보
     * @param currentUserId 현재 사용자 ID (조회 시 사용)
     * @return 상품 목록 데이터 DTO
     */
    @Transactional(readOnly = true)
    public ProductDTO.ProductListData getProductsByUser(Long targetUserId, Pageable pageable, Long currentUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, targetUserId));
        Page<Product> productPage = productRepository.findByUser(user, pageable);
        return convertToProductListData(productPage, currentUserId);
    }

    /**
     * 특정 사용자의 상품을 ID로 조회합니다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID (조회 권한 확인용)
     * @return 조회된 상품 정보 DTO
     */
    @Transactional
    public ProductDTO.Response getUserProductById(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        // 해당 유저의 상품인지 확인
        if (!product.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PRODUCT_OPERATION_FORBIDDEN, "조회");
        }

        List<Image> images = imageRepository.findByProduct_ProductId(productId);
        return mapToProductDTOResponse(product, userId);
    }

    /**
     * 모든 상품을 페이지네이션하여 조회합니다.
     *
     * @param pageable      페이지네이션 정보
     * @param currentUserId 현재 사용자 ID (조회 시 사용)
     * @return 상품 목록 데이터 DTO
     */
    @Transactional(readOnly = true)
    public ProductDTO.ProductListData getAllProducts(Pageable pageable, Long currentUserId) {
        Page<Product> productPage = productRepository.findAllByOrderByCreatedAtDesc(pageable);
        return convertToProductListData(productPage, currentUserId);
    }

    /**
     * 특정 카테고리의 상품을 페이지네이션하여 조회합니다.
     *
     * @param categoryId   카테고리 ID
     * @param pageable     페이지네이션 정보
     * @param currentUserId 현재 사용자 ID (조회 시 사용)
     * @return 상품 목록 데이터 DTO
     */
    @Transactional(readOnly = true)
    public ProductDTO.ProductListData getProductsByCategory(Integer categoryId, Pageable pageable, Long currentUserId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryId));
        Page<Product> productPage = productRepository.findByCategory(category, pageable);
        return convertToProductListData(productPage, currentUserId);
    }

    /**
     * 상품 제목으로 상품을 검색합니다.
     *
     * @param title         상품 제목
     * @param pageable      페이지네이션 정보
     * @param currentUserId 현재 사용자 ID (조회 시 사용)
     * @return 상품 목록 데이터 DTO
     */
    @Transactional(readOnly = true)
    public ProductDTO.ProductListData searchProductsByTitle(String title, Pageable pageable, Long currentUserId) {
        List<Product> productList = productRepository.findByTitleContainsIgnoreCase(title);
        Page<Product> productPage = paginateList(productList, pageable);
        return convertToProductListData(productPage, currentUserId);
    }

    /**
     * 상품 내용으로 상품을 검색합니다.
     *
     * @param keyword       상품 내용
     * @param pageable      페이지네이션 정보
     * @param currentUserId 현재 사용자 ID (조회 시 사용)
     * @return 상품 목록 데이터 DTO
     */
    @Transactional(readOnly = true)
    public ProductDTO.ProductListData searchProductsByKeyword(String keyword, Pageable pageable, Long currentUserId) {
        List<Product> productList = productRepository.findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(keyword, keyword);
        Page<Product> productPage = paginateList(productList, pageable);
        return convertToProductListData(productPage, currentUserId);
    }

    /**
     * 상품의 상태를 업데이트합니다 (예약, 완료).
     *
     * @param productId   상품 ID
     * @param isReserved  예약 상태
     * @param isCompleted 완료 상태
     * @param userId      사용자 ID (업데이트 권한 확인용)
     */
    @Transactional
    public void updateProductStatus(Long productId, Boolean isReserved, Boolean isCompleted, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        if (!product.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PRODUCT_OPERATION_FORBIDDEN, "상태 변경");
        }
            product.markAsReserved(isReserved);
            product.markAsCompleted(isCompleted);
        productRepository.save(product);
    }

    /**
     * 상품을 page로 표시합니다.
     *
     * @param list 상품 리스트
     * @param pageable
     */
    private Page<Product> paginateList(List<Product> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        if (start > end) {
            return new PageImpl<>(Collections.emptyList(), pageable, list.size());
        }
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /**
     * 상품 페이지 정보를 ProductDTO.ProductListData로 변환합니다.
     *
     * @param productPage  상품 페이지 정보
     * @param currentUserId 현재 사용자 ID (조회 시 사용)
     * @return 변환된 상품 목록 데이터 DTO
     */
    private ProductDTO.ProductListData convertToProductListData(Page<Product> productPage, Long currentUserId) {
        List<ProductDTO.Response> productResponses = productPage.getContent().stream()
                .map(product -> {
                    List<Image> images = imageRepository.findByProduct_ProductId(product.getProductId());
                    return mapToProductDTOResponse(product, currentUserId);
                })
                .collect(Collectors.toList());

        ProductDTO.ProductListPagination pagination = ProductDTO.ProductListPagination.builder()
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();

        return ProductDTO.ProductListData.builder()
                .content(productResponses)
                .pagination(pagination)
                .build();
    }

    /**
     * Product 엔티티를 ProductDTO.Response로 변환합니다.
     *
     * @param product       변환할 Product 엔티티
     * @param currentUserId 현재 사용자 ID (조회 시 사용)
     * @return 변환된 상품 정보 DTO
     */
    private ProductDTO.Response mapToProductDTOResponse(Product product, Long currentUserId) {

        return ProductDTO.Response.builder()
                .id(product.getProductId())
                .title(product.getTitle())
                .content(product.getContent())
                .price(product.getPrice())
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .seller(ProductDTO.SellerInfo.fromEntity(product.getUser()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .viewCount(product.getViewCount())
                .isReserved(product.getIsReserved() != null ? product.getIsReserved() : false)
                .isCompleted(product.getIsCompleted() != null ? product.getIsCompleted() : false)
                .build();
    }
}