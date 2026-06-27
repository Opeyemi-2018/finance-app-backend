package com.example.financeapp.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PagedResponse<T> {
    private List<T> transactions;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;           // ← number of items per page
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PagedResponse<T> from(Page<T> page) {
        return PagedResponse.<T>builder()
                .transactions(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())               // ← add this
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}