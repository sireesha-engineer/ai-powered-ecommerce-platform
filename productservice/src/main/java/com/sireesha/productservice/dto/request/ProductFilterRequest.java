package com.sireesha.productservice.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterRequest {

    private String keyword;

    private String category;

    private String brand;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;
}