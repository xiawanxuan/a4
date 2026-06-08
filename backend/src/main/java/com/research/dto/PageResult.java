package com.research.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private Long total;

    private List<T> list;

    private Integer pageNum;

    private Integer pageSize;
}
