package com.meraki.website.service;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.ProductCreateWithUrlsRequestDto;
import com.meraki.website.dto.ProductResponseDto;
import com.meraki.website.dto.ProductWithUrlsDto;
import com.meraki.website.dto.VariantWithUrlsDto;
import com.meraki.website.entity.Product;
import com.meraki.website.entity.Variant;
import com.meraki.website.repository.ProductRepository;
import com.meraki.website.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final MongoTemplate mongoTemplate;



    public ApiResponse<ProductResponseDto> createProductWithUrls(ProductCreateWithUrlsRequestDto request) {
        try {
            if (request == null || request.getProduct() == null) {
                return new ApiResponse<>(false, "Missing product payload", null);
            }

            ProductWithUrlsDto dto = request.getProduct();

            Variant defaultVariant = mapVariantUrlsDto(dto.getDefaultVariant());

            Product product = Product.builder()
                    .specification(dto.getSpecification())
                    .category(dto.getCategory())
                    .subCategory(dto.getSubCategory())
                    .collection(dto.getCollection())
                    .certificates(dto.getCertificates())
                    .defaultVariant(defaultVariant)
                    .build();

            Product savedProduct = productRepository.save(product);

            List<Variant> savedVariants = saveVariantsWithUrls(savedProduct.getId(), request.getVariantList());
            return new ApiResponse<>(true, "Product created successfully", ProductResponseDto.of(savedProduct, savedVariants));
        } catch (Exception e) {
            log.error("Product create with urls failed", e);
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<ProductResponseDto> getProductById(String id) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return new ApiResponse<>(false, "Product not found", null);
            }
            List<Variant> variants = variantRepository.findByProductId(id);
            return new ApiResponse<>(true, "Product fetched successfully", ProductResponseDto.of(product, variants));
        } catch (Exception e) {
            log.error("Product fetch failed id={}", id, e);
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<Page<Product>> listProducts(String category,String subCategory, String collection, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            Query query = new Query();
            if (category != null && !category.isBlank()) {
                query.addCriteria(Criteria.where("category").is(category));
            }
            if (subCategory != null && !subCategory.isBlank()) {
                query.addCriteria(Criteria.where("subCategory").is(subCategory));
            }
            if (collection != null && !collection.isBlank()) {
                query.addCriteria(Criteria.where("collection").is(collection));
            }

            long total = mongoTemplate.count(query, Product.class);
            query.with(pageable);
            List<Product> products = mongoTemplate.find(query, Product.class);
            return new ApiResponse<>(true, "Products fetched successfully", new PageImpl<>(products, pageable, total));
        } catch (Exception e) {
            log.error("Product list failed", e);
            return new ApiResponse<>(false, e.getMessage(), Page.empty());
        }
    }

    public ApiResponse<ProductResponseDto> updateProductWithUrls(String productId, ProductCreateWithUrlsRequestDto request) {
        try {
            if (productId == null || productId.isBlank()) {
                return new ApiResponse<>(false, "Missing product id", null);
            }
            if (request == null || request.getProduct() == null) {
                return new ApiResponse<>(false, "Missing product payload", null);
            }

            ProductWithUrlsDto dto = request.getProduct();
            if (dto.getId() != null && !dto.getId().isBlank() && !productId.equals(dto.getId())) {
                return new ApiResponse<>(false, "Product id mismatch", null);
            }

            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return new ApiResponse<>(false, "Product not found", null);
            }

            product.setCategory(dto.getCategory());
            product.setSubCategory(dto.getSubCategory());
            product.setCollection(dto.getCollection());
            product.setSpecification(dto.getSpecification());
            product.setCertificates(dto.getCertificates());
            product.setDefaultVariant(mapVariantUrlsDto(dto.getDefaultVariant()));

            Product savedProduct = productRepository.save(product);

            List<VariantWithUrlsDto> incomingVariants = request.getVariantList();
            if (incomingVariants == null) {
                incomingVariants = Collections.emptyList();
            }

            Set<String> keepIds = new HashSet<>();
            for (VariantWithUrlsDto vDto : incomingVariants) {
                if (vDto == null) continue;

                if (vDto.getId() != null && !vDto.getId().isBlank()) {
                    Variant existing = variantRepository.findById(vDto.getId()).orElse(null);
                    if (existing == null) {
                        return new ApiResponse<>(false, "Variant not found id=" + vDto.getId(), null);
                    }
                    if (existing.getProductId() == null || !productId.equals(existing.getProductId())) {
                        return new ApiResponse<>(false, "Variant does not belong to product id=" + vDto.getId(), null);
                    }

                    existing.setArticle(vDto.getArticle());
                    existing.setColor(vDto.getColor());
                    existing.setMainImageUrl(vDto.getMainImageUrl());
                    existing.setImages(vDto.getImages());
                    Variant saved = variantRepository.save(existing);
                    keepIds.add(saved.getId());
                } else {
                    Variant created = mapVariantUrlsDto(vDto);
                    created.setId(null);
                    created.setProductId(productId);
                    Variant saved = variantRepository.save(created);
                    if (saved.getId() != null) {
                        keepIds.add(saved.getId());
                    }
                }
            }

            List<Variant> existingVariants = variantRepository.findByProductId(productId);
            for (Variant v : existingVariants) {
                if (v == null) continue;
                if (v.getId() == null) continue;
                if (!keepIds.contains(v.getId())) {
                    variantRepository.deleteById(v.getId());
                }
            }

            List<Variant> finalVariants = variantRepository.findByProductId(productId);
            return new ApiResponse<>(true, "Product updated successfully", ProductResponseDto.of(savedProduct, finalVariants));
        } catch (Exception e) {
            log.error("Product update with urls failed productId={}", productId, e);
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<Void> deleteProduct(String productId) {
        try {
            if (productId == null || productId.isBlank()) {
                return new ApiResponse<>(false, "Missing product id", null);
            }
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return new ApiResponse<>(false, "Product not found", null);
            }

            variantRepository.deleteByProductId(productId);
            productRepository.deleteById(productId);
            return new ApiResponse<>(true, "Product deleted successfully", null);
        } catch (Exception e) {
            log.error("Product delete failed productId={}", productId, e);
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<Variant> updateVariant(String productId, String variantId, VariantWithUrlsDto dto) {
        try {
            if (productId == null || productId.isBlank()) {
                return new ApiResponse<>(false, "Missing product id", null);
            }
            if (variantId == null || variantId.isBlank()) {
                return new ApiResponse<>(false, "Missing variant id", null);
            }
            if (dto == null) {
                return new ApiResponse<>(false, "Missing variant payload", null);
            }

            Variant existing = variantRepository.findById(variantId).orElse(null);
            if (existing == null) {
                return new ApiResponse<>(false, "Variant not found", null);
            }
            if (existing.getProductId() == null || !productId.equals(existing.getProductId())) {
                return new ApiResponse<>(false, "Variant does not belong to product", null);
            }

            existing.setArticle(dto.getArticle());
            existing.setColor(dto.getColor());
            existing.setMainImageUrl(dto.getMainImageUrl());
            existing.setImages(dto.getImages());
            Variant saved = variantRepository.save(existing);
            return new ApiResponse<>(true, "Variant updated successfully", saved);
        } catch (Exception e) {
            log.error("Variant update failed productId={} variantId={}", productId, variantId, e);
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<Void> deleteVariant(String productId, String variantId) {
        try {
            if (productId == null || productId.isBlank()) {
                return new ApiResponse<>(false, "Missing product id", null);
            }
            if (variantId == null || variantId.isBlank()) {
                return new ApiResponse<>(false, "Missing variant id", null);
            }

            Variant existing = variantRepository.findById(variantId).orElse(null);
            if (existing == null) {
                return new ApiResponse<>(false, "Variant not found", null);
            }
            if (existing.getProductId() == null || !productId.equals(existing.getProductId())) {
                return new ApiResponse<>(false, "Variant does not belong to product", null);
            }

            variantRepository.deleteById(variantId);
            return new ApiResponse<>(true, "Variant deleted successfully", null);
        } catch (Exception e) {
            log.error("Variant delete failed productId={} variantId={}", productId, variantId, e);
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }






    private Variant mapVariantUrlsDto(VariantWithUrlsDto dto) {
        if (dto == null) {
            return new Variant();
        }
        Variant v = Variant.builder()
                .id(dto.getId())
                .article(dto.getArticle())
                .color(dto.getColor())
                .mainImageUrl(dto.getMainImageUrl())
                .images(dto.getImages())
                .build();
        return v;
    }

    private List<Variant> saveVariantsWithUrls(String productId, List<VariantWithUrlsDto> variantsDto) {
        if (variantsDto == null || variantsDto.isEmpty()) {
            return Collections.emptyList();
        }

        List<Variant> saved = new ArrayList<>();
        for (VariantWithUrlsDto dto : variantsDto) {
            if (dto == null) continue;
            Variant v = mapVariantUrlsDto(dto);
            v.setProductId(productId);
            saved.add(variantRepository.save(v));
        }
        return saved;
    }



}
