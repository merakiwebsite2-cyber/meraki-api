package com.meraki.website.controller;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.ProductCreateWithUrlsRequestDto;
import com.meraki.website.dto.ProductResponseDto;
import com.meraki.website.dto.VariantWithUrlsDto;

import com.meraki.website.entity.Product;
import com.meraki.website.entity.Variant;
import com.meraki.website.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping(
            value = {"/create-with-urls"},
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProductWithUrls(
            @RequestBody ProductCreateWithUrlsRequestDto body
    ) {
        try {
            ApiResponse<ProductResponseDto> response = productService.createProductWithUrls(body);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping(
            value = {"/{id}/update-with-urls"},
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProductWithUrls(
            @PathVariable String id,
            @RequestBody ProductCreateWithUrlsRequestDto body
    ) {
        try {
            ApiResponse<ProductResponseDto> response = productService.updateProductWithUrls(id, body);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        try {
            ApiResponse<Void> response = productService.deleteProduct(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProduct(@PathVariable String id) {
        ApiResponse<ProductResponseDto> response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) String collection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApiResponse<Page<Product>> response = productService.listProducts(category,subCategory, collection, page, size);
        return ResponseEntity.ok(response);
    }

    //not for our case we update product entirely
    @PutMapping(
            value = {"/{productId}/variants/{variantId}"},
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Variant>> updateVariant(
            @PathVariable String productId,
            @PathVariable String variantId,
            @RequestBody VariantWithUrlsDto body
    ) {
        try {
            ApiResponse<Variant> response = productService.updateVariant(productId, variantId, body);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
  //not for our case we update product entirely
    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @PathVariable String productId,
            @PathVariable String variantId
    ) {
        try {
            ApiResponse<Void> response = productService.deleteVariant(productId, variantId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

}
