package com.example.crm.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private int pageNo;
    private int pageSize;
    private long totalRecords;
    private int totalPages;
    private List<T> data;
}