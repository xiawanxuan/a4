package com.research.dto;

import lombok.Data;

import java.util.List;

@Data
public class CitationNetworkDTO {

    private List<CitationNode> nodes;

    private List<CitationEdge> edges;
}
