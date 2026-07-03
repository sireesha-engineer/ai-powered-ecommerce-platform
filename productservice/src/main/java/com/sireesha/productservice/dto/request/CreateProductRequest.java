package com.sireesha.productservice.dto.request;

import com.sireesha.productservice.entity.Brand;
import com.sireesha.productservice.entity.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank(message = "Product name is required.")
    @Size(min = 3, max = 255)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotNull(message = "Stock is required.")
    @Min(0)
    private Integer stock;

    @NotBlank(message = "Brand is required.")
    @Enumerated(EnumType.STRING)
    private Brand brand;

    @NotBlank(message = "Category is required.")
    @Enumerated(EnumType.STRING)
    private Category category;
}
