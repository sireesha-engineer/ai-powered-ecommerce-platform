package com.sireesha.productservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public class PageResponse<T> {

    private List<T> content;

    private int pageNumber;
    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;
    private boolean empty;
}
