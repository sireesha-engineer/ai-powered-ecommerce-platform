package com.sireesha.productservice.repository;

import com.sireesha.productservice.entity.Product;
import com.sireesha.productservice.entity.ProductStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByName(String name);

    List<Product> findByStatus(ProductStatus status);
    //Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategory(String category);

    List<Product> findByBrand(String brand);
    Page<Product> findByStatusNot(ProductStatus status, Pageable pageable);
}
