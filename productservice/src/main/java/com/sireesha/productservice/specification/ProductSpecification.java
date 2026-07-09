package com.sireesha.productservice.specification;

import com.sireesha.productservice.dto.request.ProductFilterRequest;
import com.sireesha.productservice.entity.Product;
import com.sireesha.productservice.entity.ProductStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    public static Specification<Product> filterProducts(ProductFilterRequest request) {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Ignore deleted products
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.notEqual(root.get("status"), ProductStatus.DELETED));

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"
                                + request.getKeyword().toLowerCase() + "%"));
            }

            if (request.getCategory() != null && !request.getCategory().isBlank()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("category"), request.getCategory())
                );
            }

            if (request.getBrand() != null && !request.getBrand().isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("brand"), request.getBrand()));
            }

            if (request.getMinPrice() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice())
                );
            }

            if (request.getMaxPrice() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }
            return predicate;
        });
    }
}
