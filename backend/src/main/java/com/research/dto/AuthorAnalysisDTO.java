package com.research.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AuthorAnalysisDTO {

    private List<AuthorDTO> coreAuthors;

    private List<PublicationTrend> publicationTrend;

    private List<AuthorDTO> collaborationNodes;

    private List<CollaborationEdge> collaborationEdges;

    @Data
    public static class PublicationTrend {
        private Integer year;
        private Integer count;
    }

    @Data
    public static class CollaborationEdge {
        private Long source;
        private Long target;
        private Integer weight;
    }
}
