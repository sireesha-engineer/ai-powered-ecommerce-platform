package com.sireesha.productservice.utils;

import java.util.Set;

public class Constants {
    public static final Set<String> PRODUCT_ALLOWED_SORT_FIELDS = Set.of(
            "name",
            "price",
            "stock",
            "brand",
            "category",
            "createdAt"
    );
}
