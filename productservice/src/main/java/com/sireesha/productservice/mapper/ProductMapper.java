package com.sireesha.productservice.mapper;

import com.sireesha.productservice.dto.request.CreateProductRequest;
import com.sireesha.productservice.dto.request.UpdateProductRequest;
import com.sireesha.productservice.dto.response.ProductResponse;
import com.sireesha.productservice.entity.Product;
import com.sireesha.productservice.entity.ProductStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .brand(request.getBrand())
                .category(request.getCategory())
                .status(ProductStatus.ACTIVE)
                .build();
    }

    public ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .brand(product.getBrand())
                .category(product.getCategory())
                .status(product.getStatus())
                .build();
    }

    public void updateEntity(Product product,
                             UpdateProductRequest request) {

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
    }
}
