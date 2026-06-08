package com.research.dto;

import lombok.Data;

import java.util.List;

@Data
public class InstitutionAnalysisDTO {

    private List<InstitutionDTO> institutionRanking;

    private List<PublicationTrend> publicationTrend;

    private List<InstitutionDTO> collaborationNodes;

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
