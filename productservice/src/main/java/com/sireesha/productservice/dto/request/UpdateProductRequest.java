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
public class UpdateProductRequest {

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private Brand brand;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private Category category;
}
