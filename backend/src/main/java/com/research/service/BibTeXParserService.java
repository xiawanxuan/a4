package com.research.service;

import com.research.dto.PaperImportDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BibTeXParserService {

    private static final Pattern ENTRY_START_PATTERN = Pattern.compile(
            "@(\\w+)\\s*\\{\\s*([^,]+)\\s*,",
            Pattern.CASE_INSENSITIVE
    );

    public List<PaperImportDTO> parse(String bibtexContent) {
        List<PaperImportDTO> papers = new ArrayList<>();

        if (bibtexContent == null || bibtexContent.trim().isEmpty()) {
            return papers;
        }

        List<String> entries = splitEntries(bibtexContent);

        for (String entry : entries) {
            try {
                PaperImportDTO dto = parseEntry(entry);
                if (dto != null && dto.getTitle() != null && !dto.getTitle().isEmpty()) {
                    papers.add(dto);
                }
            } catch (Exception e) {
                continue;
            }
        }

        return papers;
    }

    private List<String> splitEntries(String content) {
        List<String> entries = new ArrayList<>();
        int depth = 0;
        int start = -1;
        boolean inEntry = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '@' && !inEntry) {
                Matcher matcher = ENTRY_START_PATTERN.matcher(content.substring(i));
                if (matcher.find()) {
                    start = i;
                    inEntry = true;
                    depth = 0;
                }
            }

            if (inEntry) {
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        entries.add(content.substring(start, i + 1));
                        inEntry = false;
                        start = -1;
                    }
                }
            }
        }

        return entries;
    }

    private PaperImportDTO parseEntry(String entry) {
        Matcher typeMatcher = Pattern.compile("@(\\w+)\\s*\\{\\s*([^,]+)\\s*,").matcher(entry);
        if (!typeMatcher.find()) {
            return null;
        }

        String entryType = typeMatcher.group(1).toLowerCase();
        String citationKey = typeMatcher.group(2).trim();

        PaperImportDTO dto = new PaperImportDTO();
        dto.setDocumentType(entryType);

        String fieldsContent = entry.substring(typeMatcher.end(), entry.length() - 1);

        List<String[]> fields = parseFields(fieldsContent);

        for (String[] field : fields) {
            String fieldName = field[0].toLowerCase().trim();
            String fieldValue = cleanBibTeXValue(field[1]);
            mapField(dto, fieldName, fieldValue);
        }

        return dto;
    }

    private List<String[]> parseFields(String content) {
        List<String[]> fields = new ArrayList<>();
        int i = 0;
        int length = content.length();

        while (i < length) {
            while (i < length && Character.isWhitespace(content.charAt(i))) {
                i++;
            }

            if (i >= length) break;

            int nameStart = i;
            while (i < length && content.charAt(i) != '=' && content.charAt(i) != '{') {
                i++;
            }

            if (i >= length || content.charAt(i) != '=') {
                break;
            }

            String name = content.substring(nameStart, i).trim();
            i++;

            while (i < length && Character.isWhitespace(content.charAt(i))) {
                i++;
            }

            String value = "";
            if (i < length && content.charAt(i) == '{') {
                int depth = 1;
                i++;
                StringBuilder valueBuilder = new StringBuilder();
                while (i < length && depth > 0) {
                    char c = content.charAt(i);
                    if (c == '{') {
                        depth++;
                        valueBuilder.append(c);
                    } else if (c == '}') {
                        depth--;
                        if (depth > 0) {
                            valueBuilder.append(c);
                        }
                    } else {
                        valueBuilder.append(c);
                    }
                    i++;
                }
                value = valueBuilder.toString();
            } else if (i < length && content.charAt(i) == '"') {
                i++;
                StringBuilder valueBuilder = new StringBuilder();
                while (i < length && content.charAt(i) != '"') {
                    valueBuilder.append(content.charAt(i));
                    i++;
                }
                if (i < length) i++;
                value = valueBuilder.toString();
            } else {
                StringBuilder valueBuilder = new StringBuilder();
                while (i < length && content.charAt(i) != ',' && content.charAt(i) != '\n') {
                    valueBuilder.append(content.charAt(i));
                    i++;
                }
                value = valueBuilder.toString().trim();
            }

            fields.add(new String[]{name, value});

            while (i < length && content.charAt(i) != ',') {
                i++;
            }
            if (i < length) i++;
        }

        return fields;
    }

    private void mapField(PaperImportDTO dto, String fieldName, String fieldValue) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            return;
        }

        switch (fieldName) {
            case "title":
                dto.setTitle(fieldValue);
                break;
            case "author":
                List<String> authors = parseAuthors(fieldValue);
                if (!authors.isEmpty()) {
                    dto.setAuthors(authors);
                }
                break;
            case "abstract":
                dto.setAbstractText(fieldValue);
                break;
            case "keywords":
            case "keyword":
                dto.setKeywords(fieldValue);
                break;
            case "doi":
                dto.setDoi(fieldValue);
                break;
            case "pmid":
                dto.setPmid(fieldValue);
                break;
            case "arxiv":
            case "arxivId":
            case "arxiv_id":
                dto.setArxivId(fieldValue);
                break;
            case "url":
            case "urlraw":
            case "url_raw":
                dto.setUrl(fieldValue);
                break;
            case "pdf":
            case "pdfurl":
            case "pdf_url":
                dto.setPdfUrl(fieldValue);
                break;
            case "journal":
            case "journaltitle":
            case "journal_title":
            case "booktitle":
                dto.setJournalName(fieldValue);
                break;
            case "issn":
                dto.setIssn(fieldValue);
                break;
            case "volume":
            case "vol":
                dto.setVolume(fieldValue);
                break;
            case "number":
            case "issue":
            case "num":
                dto.setIssue(fieldValue);
                break;
            case "pages":
            case "page":
                dto.setPages(fieldValue.replace("--", "-"));
                break;
            case "year":
                try {
                    String yearStr = fieldValue.trim().replaceAll("[^0-9]", "");
                    if (yearStr.length() >= 4) {
                        dto.setPublicationYear(Integer.parseInt(yearStr.substring(0, 4)));
                    }
                } catch (NumberFormatException ignored) {
                }
                break;
            case "date":
            case "pubdate":
            case "publication_date":
                parseDate(dto, fieldValue);
                break;
            case "month":
                break;
            case "publisher":
                break;
            case "language":
            case "lang":
                dto.setLanguage(fieldValue);
                break;
            case "note":
            case "annote":
            case "annotation":
                break;
            case "address":
            case "location":
                break;
            case "edition":
                break;
            case "series":
                break;
            case "isbn":
                break;
            default:
                break;
        }
    }

    private List<String> parseAuthors(String authorsStr) {
        List<String> authors = new ArrayList<>();

        if (authorsStr == null || authorsStr.trim().isEmpty()) {
            return authors;
        }

        String[] parts = authorsStr.split("\\s+and\\s+", -1);
        for (String part : parts) {
            String author = part.trim();
            if (!author.isEmpty()) {
                author = formatAuthorName(author);
                authors.add(author);
            }
        }

        return authors;
    }

    private String formatAuthorName(String author) {
        if (author == null || author.isEmpty()) {
            return author;
        }

        author = author.trim();

        if (author.contains(",")) {
            String[] nameParts = author.split(",");
            if (nameParts.length >= 2) {
                String lastName = nameParts[0].trim();
                String firstName = nameParts[1].trim();
                return firstName + " " + lastName;
            }
        }

        return author;
    }

    private String cleanBibTeXValue(String value) {
        if (value == null) return "";

        String result = value.trim();

        result = result.replaceAll("[\\{\\}]", "");

        result = result.replaceAll("\\\\\"a", "ä");
        result = result.replaceAll("\\\\\"o", "ö");
        result = result.replaceAll("\\\\\"u", "ü");
        result = result.replaceAll("\\\\\"A", "Ä");
        result = result.replaceAll("\\\\\"O", "Ö");
        result = result.replaceAll("\\\\\"U", "Ü");
        result = result.replaceAll("\\\\ss", "ß");
        result = result.replaceAll("\\\\'e", "é");
        result = result.replaceAll("\\\\'E", "É");
        result = result.replaceAll("\\\\`e", "è");
        result = result.replaceAll("\\\\~n", "ñ");
        result = result.replaceAll("\\\\~N", "Ñ");
        result = result.replaceAll("\\\\\\^e", "ê");
        result = result.replaceAll("\\\\\\^a", "â");
        result = result.replaceAll("\\\\\\^o", "ô");

        result = result.replaceAll("\\\\[a-zA-Z]+\\s*", "");

        return result.trim();
    }

    private void parseDate(PaperImportDTO dto, String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return;

        Pattern yearPattern = Pattern.compile("(19|20)\\d{2}");
        Matcher matcher = yearPattern.matcher(dateStr);
        if (matcher.find()) {
            try {
                dto.setPublicationYear(Integer.parseInt(matcher.group()));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
