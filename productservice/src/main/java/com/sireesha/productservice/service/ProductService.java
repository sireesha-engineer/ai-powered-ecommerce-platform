package com.sireesha.productservice.service;

import com.sireesha.productservice.dto.request.CreateProductRequest;
import com.sireesha.productservice.dto.request.UpdateProductRequest;
import com.sireesha.productservice.dto.response.PageResponse;
import com.sireesha.productservice.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;


public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProductById(Long productId);

    PageResponse<ProductResponse> getAllProducts(Pageable pageable);

    ProductResponse updateProduct(Long productId,
                                  UpdateProductRequest request);

    void deleteProduct(Long productId);
}
