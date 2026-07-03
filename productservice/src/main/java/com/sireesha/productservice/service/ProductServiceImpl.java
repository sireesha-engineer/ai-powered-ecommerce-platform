package com.sireesha.productservice.service;

import com.sireesha.productservice.dto.request.CreateProductRequest;
import com.sireesha.productservice.dto.request.UpdateProductRequest;
import com.sireesha.productservice.dto.response.PageResponse;
import com.sireesha.productservice.dto.response.ProductResponse;
import com.sireesha.productservice.entity.Product;
import com.sireesha.productservice.entity.ProductStatus;
import com.sireesha.productservice.exception.DuplicateResourceException;
import com.sireesha.productservice.exception.ResourceNotFoundException;
import com.sireesha.productservice.mapper.PaginationMapper;
import com.sireesha.productservice.mapper.ProductMapper;
import com.sireesha.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;
    private final PaginationMapper paginationMapper;
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Product with name '" + request.getName() + "' already exists.");
        }

        Product product = productMapper.toEntity(request);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id : " + productId));

        return productMapper.toResponse(product);
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> page = productRepository.findByStatusNot(ProductStatus.DELETED, pageable);

        Page<ProductResponse> responsePage = page.map(productMapper::toResponse);

        return paginationMapper.toPageResponse(responsePage);
    }

    @Override
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id : " + productId));
        if (!product.getName().equals(request.getName())
                && productRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Product with name '" + request.getName() + "' already exists.");
        }
        productMapper.updateEntity(product, request);

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id : " + productId));

        product.setStatus(ProductStatus.DELETED);

        productRepository.save(product);
    }
}
