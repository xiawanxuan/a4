package com.research.service;

import com.research.dto.*;
import com.research.entity.*;
import com.research.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final PaperRepository paperRepository;
    private final AuthorRepository authorRepository;
    private final InstitutionRepository institutionRepository;
    private final JournalRepository journalRepository;
    private final CitationRepository citationRepository;
    private final PaperAuthorRepository paperAuthorRepository;
    private final PaperInstitutionRepository paperInstitutionRepository;

    @Transactional(readOnly = true)
    public AuthorAnalysisDTO analyzeCoreAuthors(int topN, String sortBy) {
        List<Author> allAuthors = authorRepository.findAll();
        List<AuthorDTO> authorDTOs = allAuthors.stream()
                .map(this::convertToAuthorDTO)
                .collect(Collectors.toList());

        switch (sortBy.toLowerCase()) {
            case "publications":
                authorDTOs.sort((a, b) -> Integer.compare(
                        b.getTotalPublications() != null ? b.getTotalPublications() : 0,
                        a.getTotalPublications() != null ? a.getTotalPublications() : 0
                ));
                break;
            case "citations":
                authorDTOs.sort((a, b) -> Integer.compare(
                        b.getTotalCitations() != null ? b.getTotalCitations() : 0,
                        a.getTotalCitations() != null ? a.getTotalCitations() : 0
                ));
                break;
            case "hindex":
            default:
                authorDTOs.sort((a, b) -> Integer.compare(
                        b.getHIndex() != null ? b.getHIndex() : 0,
                        a.getHIndex() != null ? a.getHIndex() : 0
                ));
                break;
        }

        List<AuthorDTO> topAuthors = authorDTOs.stream()
                .limit(topN)
                .collect(Collectors.toList());

        AuthorAnalysisDTO result = new AuthorAnalysisDTO();
        result.setCoreAuthors(topAuthors);
        return result;
    }

    @Transactional(readOnly = true)
    public InstitutionAnalysisDTO analyzeCoreInstitutions(int topN, String sortBy) {
        List<Institution> allInstitutions = institutionRepository.findAll();
        List<InstitutionDTO> institutionDTOs = allInstitutions.stream()
                .map(this::convertToInstitutionDTO)
                .collect(Collectors.toList());

        switch (sortBy.toLowerCase()) {
            case "citations":
                institutionDTOs.sort((a, b) -> Integer.compare(
                        b.getTotalCitations() != null ? b.getTotalCitations() : 0,
                        a.getTotalCitations() != null ? a.getTotalCitations() : 0
                ));
                break;
            case "publications":
            default:
                institutionDTOs.sort((a, b) -> Integer.compare(
                        b.getPaperCount() != null ? b.getPaperCount() : 0,
                        a.getPaperCount() != null ? a.getPaperCount() : 0
                ));
                break;
        }

        List<InstitutionDTO> topInstitutions = institutionDTOs.stream()
                .limit(topN)
                .collect(Collectors.toList());

        InstitutionAnalysisDTO result = new InstitutionAnalysisDTO();
        result.setInstitutionRanking(topInstitutions);
        return result;
    }

    @Transactional(readOnly = true)
    public List<AuthorAnalysisDTO.PublicationTrend> analyzePublicationTrend() {
        List<Paper> allPapers = paperRepository.findAll();
        Map<Integer, Long> yearCountMap = allPapers.stream()
                .filter(p -> p.getPublicationYear() != null)
                .collect(Collectors.groupingBy(Paper::getPublicationYear, Collectors.counting()));

        List<AuthorAnalysisDTO.PublicationTrend> trends = yearCountMap.entrySet().stream()
                .map(entry -> {
                    AuthorAnalysisDTO.PublicationTrend trend = new AuthorAnalysisDTO.PublicationTrend();
                    trend.setYear(entry.getKey());
                    trend.setCount(entry.getValue().intValue());
                    return trend;
                })
                .sorted(Comparator.comparingInt(AuthorAnalysisDTO.PublicationTrend::getYear))
                .collect(Collectors.toList());

        return trends;
    }

    @Transactional(readOnly = true)
    public List<KeywordCooccurrence> analyzeKeywordCooccurrence(int topN) {
        List<Paper> allPapers = paperRepository.findAll();
        Map<String, Integer> keywordFrequency = new HashMap<>();
        Map<String, Map<String, Integer>> cooccurrenceMap = new HashMap<>();

        for (Paper paper : allPapers) {
            if (paper.getKeywords() == null || paper.getKeywords().isEmpty()) {
                continue;
            }
            List<String> keywords = Arrays.stream(paper.getKeywords().split("[,，;；]"))
                    .map(String::trim)
                    .filter(k -> !k.isEmpty())
                    .collect(Collectors.toList());

            for (String keyword : keywords) {
                keywordFrequency.merge(keyword, 1, Integer::sum);
            }

            for (int i = 0; i < keywords.size(); i++) {
                for (int j = i + 1; j < keywords.size(); j++) {
                    String k1 = keywords.get(i);
                    String k2 = keywords.get(j);
                    String pairKey = k1.compareTo(k2) < 0 ? k1 + "||" + k2 : k2 + "||" + k1;
                    cooccurrenceMap.computeIfAbsent(pairKey, k -> new HashMap<>())
                            .merge("count", 1, Integer::sum);
                }
            }
        }

        List<KeywordCooccurrence> cooccurrences = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : cooccurrenceMap.entrySet()) {
            String[] parts = entry.getKey().split("\\|\\|");
            KeywordCooccurrence kc = new KeywordCooccurrence();
            kc.setKeyword1(parts[0]);
            kc.setKeyword2(parts[1]);
            kc.setCount(entry.getValue().get("count"));
            cooccurrences.add(kc);
        }

        cooccurrences.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));
        return cooccurrences.stream()
                .limit(topN)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthorAnalysisDTO analyzeAuthorCollaboration(int topN) {
        List<PaperAuthor> allPaperAuthors = paperAuthorRepository.findAll();
        Map<Long, List<Long>> paperAuthorsMap = allPaperAuthors.stream()
                .collect(Collectors.groupingBy(
                        PaperAuthor::getPaperId,
                        Collectors.mapping(PaperAuthor::getAuthorId, Collectors.toList())
                ));

        Map<String, Integer> collaborationCount = new HashMap<>();
        Set<Long> authorIds = new HashSet<>();

        for (List<Long> authors : paperAuthorsMap.values()) {
            authorIds.addAll(authors);
            for (int i = 0; i < authors.size(); i++) {
                for (int j = i + 1; j < authors.size(); j++) {
                    Long a1 = authors.get(i);
                    Long a2 = authors.get(j);
                    String pairKey = a1 < a2 ? a1 + "_" + a2 : a2 + "_" + a1;
                    collaborationCount.merge(pairKey, 1, Integer::sum);
                }
            }
        }

        List<AuthorAnalysisDTO.CollaborationEdge> edges = collaborationCount.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("_");
                    AuthorAnalysisDTO.CollaborationEdge edge = new AuthorAnalysisDTO.CollaborationEdge();
                    edge.setSource(Long.parseLong(parts[0]));
                    edge.setTarget(Long.parseLong(parts[1]));
                    edge.setWeight(entry.getValue());
                    return edge;
                })
                .sorted((a, b) -> Integer.compare(b.getWeight(), a.getWeight()))
                .limit(topN)
                .collect(Collectors.toList());

        Set<Long> connectedAuthorIds = new HashSet<>();
        for (AuthorAnalysisDTO.CollaborationEdge edge : edges) {
            connectedAuthorIds.add(edge.getSource());
            connectedAuthorIds.add(edge.getTarget());
        }

        List<AuthorDTO> nodes = authorRepository.findAllById(connectedAuthorIds).stream()
                .map(this::convertToAuthorDTO)
                .collect(Collectors.toList());

        AuthorAnalysisDTO result = new AuthorAnalysisDTO();
        result.setCollaborationNodes(nodes);
        result.setCollaborationEdges(edges);
        return result;
    }

    @Transactional(readOnly = true)
    public InstitutionAnalysisDTO analyzeInstitutionCollaboration(int topN) {
        List<PaperInstitution> allPaperInstitutions = paperInstitutionRepository.findAll();
        Map<Long, List<Long>> paperInstitutionsMap = allPaperInstitutions.stream()
                .collect(Collectors.groupingBy(
                        PaperInstitution::getPaperId,
                        Collectors.mapping(PaperInstitution::getInstitutionId, Collectors.toList())
                ));

        Map<String, Integer> collaborationCount = new HashMap<>();
        Set<Long> institutionIds = new HashSet<>();

        for (List<Long> institutions : paperInstitutionsMap.values()) {
            institutionIds.addAll(institutions);
            for (int i = 0; i < institutions.size(); i++) {
                for (int j = i + 1; j < institutions.size(); j++) {
                    Long i1 = institutions.get(i);
                    Long i2 = institutions.get(j);
                    String pairKey = i1 < i2 ? i1 + "_" + i2 : i2 + "_" + i1;
                    collaborationCount.merge(pairKey, 1, Integer::sum);
                }
            }
        }

        List<InstitutionAnalysisDTO.CollaborationEdge> edges = collaborationCount.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("_");
                    InstitutionAnalysisDTO.CollaborationEdge edge = new InstitutionAnalysisDTO.CollaborationEdge();
                    edge.setSource(Long.parseLong(parts[0]));
                    edge.setTarget(Long.parseLong(parts[1]));
                    edge.setWeight(entry.getValue());
                    return edge;
                })
                .sorted((a, b) -> Integer.compare(b.getWeight(), a.getWeight()))
                .limit(topN)
                .collect(Collectors.toList());

        Set<Long> connectedInstitutionIds = new HashSet<>();
        for (InstitutionAnalysisDTO.CollaborationEdge edge : edges) {
            connectedInstitutionIds.add(edge.getSource());
            connectedInstitutionIds.add(edge.getTarget());
        }

        List<InstitutionDTO> nodes = institutionRepository.findAllById(connectedInstitutionIds).stream()
                .map(this::convertToInstitutionDTO)
                .collect(Collectors.toList());

        InstitutionAnalysisDTO result = new InstitutionAnalysisDTO();
        result.setCollaborationNodes(nodes);
        result.setCollaborationEdges(edges);
        return result;
    }

    @Transactional(readOnly = true)
    public List<JournalDistribution> analyzeJournalDistribution(int topN) {
        List<Paper> allPapers = paperRepository.findAll();
        Map<Long, Long> journalCountMap = allPapers.stream()
                .filter(p -> p.getJournalId() != null)
                .collect(Collectors.groupingBy(Paper::getJournalId, Collectors.counting()));

        List<JournalDistribution> distributions = journalCountMap.entrySet().stream()
                .map(entry -> {
                    JournalDistribution jd = new JournalDistribution();
                    journalRepository.findById(entry.getKey()).ifPresent(journal -> {
                        jd.setJournalId(journal.getId());
                        jd.setJournalName(journal.getName());
                    });
                    jd.setPaperCount(entry.getValue().intValue());
                    return jd;
                })
                .sorted((a, b) -> Integer.compare(b.getPaperCount(), a.getPaperCount()))
                .limit(topN)
                .collect(Collectors.toList());

        return distributions;
    }

    @Transactional(readOnly = true)
    public List<ResearchArea> analyzeResearchAreas(int topN) {
        List<Paper> allPapers = paperRepository.findAll();
        Map<String, Integer> keywordFrequency = new HashMap<>();
        Map<String, Integer> keywordCitations = new HashMap<>();

        for (Paper paper : allPapers) {
            if (paper.getKeywords() == null || paper.getKeywords().isEmpty()) {
                continue;
            }
            List<String> keywords = Arrays.stream(paper.getKeywords().split("[,，;；]"))
                    .map(String::trim)
                    .filter(k -> !k.isEmpty())
                    .collect(Collectors.toList());

            int citations = paper.getTotalCitations() != null ? paper.getTotalCitations() : 0;
            for (String keyword : keywords) {
                keywordFrequency.merge(keyword, 1, Integer::sum);
                keywordCitations.merge(keyword, citations, Integer::sum);
            }
        }

        List<ResearchArea> researchAreas = keywordFrequency.entrySet().stream()
                .map(entry -> {
                    ResearchArea ra = new ResearchArea();
                    ra.setKeyword(entry.getKey());
                    ra.setPaperCount(entry.getValue());
                    ra.setTotalCitations(keywordCitations.getOrDefault(entry.getKey(), 0));
                    return ra;
                })
                .sorted((a, b) -> Integer.compare(b.getPaperCount(), a.getPaperCount()))
                .limit(topN)
                .collect(Collectors.toList());

        return researchAreas;
    }

    @Transactional(readOnly = true)
    public List<PaperDTO> getTopCitedPapers(int topN) {
        List<Paper> topPapers = paperRepository.findAllOrderByTotalCitationsDesc(PageRequest.of(0, topN)).getContent();
        return topPapers.stream()
                .map(this::convertToPaperDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DataOverview getDataOverview() {
        long paperCount = paperRepository.count();
        long authorCount = authorRepository.count();
        long institutionCount = institutionRepository.count();

        List<Paper> allPapers = paperRepository.findAll();
        int totalCitations = allPapers.stream()
                .mapToInt(p -> p.getTotalCitations() != null ? p.getTotalCitations() : 0)
                .sum();

        DataOverview overview = new DataOverview();
        overview.setTotalPapers((int) paperCount);
        overview.setTotalAuthors((int) authorCount);
        overview.setTotalInstitutions((int) institutionCount);
        overview.setTotalCitations(totalCitations);
        return overview;
    }

    private AuthorDTO convertToAuthorDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setNameEn(author.getNameEn());
        dto.setOrcid(author.getOrcid());
        dto.setEmail(author.getEmail());
        dto.setHomepage(author.getHomepage());
        dto.setHIndex(author.getHIndex());
        dto.setTotalCitations(author.getTotalCitations());
        dto.setTotalPublications(author.getTotalPublications());
        dto.setAffiliationId(author.getAffiliationId());
        dto.setBiography(author.getBiography());
        return dto;
    }

    private InstitutionDTO convertToInstitutionDTO(Institution institution) {
        InstitutionDTO dto = new InstitutionDTO();
        dto.setId(institution.getId());
        dto.setName(institution.getName());
        dto.setNameEn(institution.getNameEn());
        dto.setCountry(institution.getCountry());
        dto.setCity(institution.getCity());
        dto.setDepartment(institution.getDepartment());
        dto.setType(institution.getType());
        dto.setDescription(institution.getDescription());
        return dto;
    }

    private PaperDTO convertToPaperDTO(Paper paper) {
        PaperDTO dto = new PaperDTO();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setTitleEn(paper.getTitleEn());
        dto.setAbstractText(paper.getAbstractText());
        dto.setKeywords(paper.getKeywords());
        dto.setDoi(paper.getDoi());
        dto.setPmid(paper.getPmid());
        dto.setArxivId(paper.getArxivId());
        dto.setUrl(paper.getUrl());
        dto.setPdfUrl(paper.getPdfUrl());
        dto.setJournalId(paper.getJournalId());
        dto.setVolume(paper.getVolume());
        dto.setIssue(paper.getIssue());
        dto.setPages(paper.getPages());
        dto.setPublicationDate(paper.getPublicationDate());
        dto.setPublicationYear(paper.getPublicationYear());
        dto.setLanguage(paper.getLanguage());
        dto.setDocumentType(paper.getDocumentType());
        dto.setTotalCitations(paper.getTotalCitations());
        dto.setTotalReferences(paper.getTotalReferences());
        return dto;
    }

    public static class KeywordCooccurrence {
        private String keyword1;
        private String keyword2;
        private Integer count;

        public String getKeyword1() { return keyword1; }
        public void setKeyword1(String keyword1) { this.keyword1 = keyword1; }
        public String getKeyword2() { return keyword2; }
        public void setKeyword2(String keyword2) { this.keyword2 = keyword2; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }

    public static class JournalDistribution {
        private Long journalId;
        private String journalName;
        private Integer paperCount;

        public Long getJournalId() { return journalId; }
        public void setJournalId(Long journalId) { this.journalId = journalId; }
        public String getJournalName() { return journalName; }
        public void setJournalName(String journalName) { this.journalName = journalName; }
        public Integer getPaperCount() { return paperCount; }
        public void setPaperCount(Integer paperCount) { this.paperCount = paperCount; }
    }

    public static class ResearchArea {
        private String keyword;
        private Integer paperCount;
        private Integer totalCitations;

        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public Integer getPaperCount() { return paperCount; }
        public void setPaperCount(Integer paperCount) { this.paperCount = paperCount; }
        public Integer getTotalCitations() { return totalCitations; }
        public void setTotalCitations(Integer totalCitations) { this.totalCitations = totalCitations; }
    }

    public static class DataOverview {
        private Integer totalPapers;
        private Integer totalAuthors;
        private Integer totalInstitutions;
        private Integer totalCitations;

        public Integer getTotalPapers() { return totalPapers; }
        public void setTotalPapers(Integer totalPapers) { this.totalPapers = totalPapers; }
        public Integer getTotalAuthors() { return totalAuthors; }
        public void setTotalAuthors(Integer totalAuthors) { this.totalAuthors = totalAuthors; }
        public Integer getTotalInstitutions() { return totalInstitutions; }
        public void setTotalInstitutions(Integer totalInstitutions) { this.totalInstitutions = totalInstitutions; }
        public Integer getTotalCitations() { return totalCitations; }
        public void setTotalCitations(Integer totalCitations) { this.totalCitations = totalCitations; }
    }
}
