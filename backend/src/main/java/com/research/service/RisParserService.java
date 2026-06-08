package com.research.service;

import com.research.dto.PaperImportDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class RisParserService {

    public List<PaperImportDTO> parse(String risContent) {
        List<PaperImportDTO> papers = new ArrayList<>();

        if (risContent == null || risContent.trim().isEmpty()) {
            return papers;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(risContent))) {
            PaperImportDTO currentPaper = null;
            List<String> currentAuthors = new ArrayList<>();
            List<String> currentKeywords = new ArrayList<>();
            String startPage = null;
            String endPage = null;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("TY  - ")) {
                    currentPaper = new PaperImportDTO();
                    currentAuthors = new ArrayList<>();
                    currentKeywords = new ArrayList<>();
                    startPage = null;
                    endPage = null;

                    String type = line.substring(6).trim();
                    currentPaper.setDocumentType(type);
                } else if (line.startsWith("ER  - ")) {
                    if (currentPaper != null) {
                        if (!currentAuthors.isEmpty()) {
                            currentPaper.setAuthors(currentAuthors);
                        }
                        if (!currentKeywords.isEmpty()) {
                            currentPaper.setKeywords(String.join(", ", currentKeywords));
                        }
                        if (startPage != null) {
                            if (endPage != null) {
                                currentPaper.setPages(startPage + "-" + endPage);
                            } else {
                                currentPaper.setPages(startPage);
                            }
                        }
                        papers.add(currentPaper);
                        currentPaper = null;
                    }
                } else if (currentPaper != null && line.contains("  - ")) {
                    String tag = line.substring(0, 2);
                    String value = line.substring(6).trim();

                    if (tag.equals("SP")) {
                        startPage = value;
                    } else if (tag.equals("EP")) {
                        endPage = value;
                    } else {
                        mapField(currentPaper, currentAuthors, currentKeywords, tag, value);
                    }
                }
            }

            if (currentPaper != null) {
                if (!currentAuthors.isEmpty()) {
                    currentPaper.setAuthors(currentAuthors);
                }
                if (!currentKeywords.isEmpty()) {
                    currentPaper.setKeywords(String.join(", ", currentKeywords));
                }
                if (startPage != null) {
                    if (endPage != null) {
                        currentPaper.setPages(startPage + "-" + endPage);
                    } else {
                        currentPaper.setPages(startPage);
                    }
                }
                papers.add(currentPaper);
            }

        } catch (Exception e) {
            throw new RuntimeException("解析RIS文件失败: " + e.getMessage(), e);
        }

        return papers;
    }

    private void mapField(PaperImportDTO dto, List<String> authors, List<String> keywords,
                         String tag, String value) {
        switch (tag) {
            case "TI":
            case "T1":
            case "CT":
                dto.setTitle(value);
                break;
            case "AU":
            case "A1":
            case "A2":
            case "A3":
            case "A4":
                authors.add(value);
                break;
            case "AB":
            case "N2":
                dto.setAbstractText(value);
                break;
            case "KW":
                keywords.add(value);
                break;
            case "DO":
                dto.setDoi(value);
                break;
            case "PM":
                dto.setPmid(value);
                break;
            case "UR":
            case "L1":
                dto.setUrl(value);
                break;
            case "JO":
            case "JF":
            case "J2":
            case "JA":
            case "T2":
                dto.setJournalName(value);
                break;
            case "SN":
                dto.setIssn(value);
                break;
            case "VL":
                dto.setVolume(value);
                break;
            case "IS":
            case "CP":
                dto.setIssue(value);
                break;
            case "PY":
            case "Y1":
                try {
                    String yearStr = value.trim();
                    if (yearStr.length() >= 4) {
                        yearStr = yearStr.substring(0, 4);
                        dto.setPublicationYear(Integer.parseInt(yearStr));
                    }
                } catch (NumberFormatException ignored) {
                }
                break;
            case "DA":
                parseDate(dto, value);
                break;
            case "LA":
                dto.setLanguage(value);
                break;
            case "PB":
                break;
            case "ET":
                break;
            default:
                break;
        }
    }

    private void parseDate(PaperImportDTO dto, String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return;

        try {
            if (dateStr.length() >= 4) {
                String yearStr = dateStr.substring(0, 4);
                dto.setPublicationYear(Integer.parseInt(yearStr));
            }
        } catch (NumberFormatException ignored) {
        }
    }
}
