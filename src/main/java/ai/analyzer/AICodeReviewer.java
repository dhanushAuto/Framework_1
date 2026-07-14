package ai.analyzer;

import ai.service.AIService;
import java.util.ArrayList;
import java.util.List;

public class AICodeReviewer {

    private final AIService aiService = new AIService();

    public String ask(String prompt) {
        return aiService.ask(prompt);
    }

    public String reviewCode(String filePath, String sourceCode) {
        String prompt = buildReviewPrompt(filePath, sourceCode);
        return aiService.ask(prompt);
    }
    private String buildReviewPrompt(String filePath, String sourceCode) {
        return """
            You are a Senior Java Code Reviewer.
            Analyze the following code from file: %s
            
            Focus on:
            1. Naming Conventions (classes, methods, variables)
            2. Code Complexity (nested loops, long methods)
            3. Null Safety (potential NPEs)
            4. Best Practices (DRY, SOLID)
            5. Potential Bugs
            6. Security concerns
            
            Provide findings in the following format:
            LINE: [number]
            SEVERITY: [CRITICAL/HIGH/MEDIUM/LOW]
            CATEGORY: [Naming/Complexity/Safety/Best Practice/Bug/Security]
            MESSAGE: [Description of the issue and how to fix it]
            ---
            
            CODE:
            %s
            """.formatted(filePath, sourceCode);
    }

    public List<CodeReviewFinding> getFindings(String filePath, String sourceCode) {
        String response = reviewCode(filePath, sourceCode);
        return parseFindings(response, filePath);
    }

    private List<CodeReviewFinding> parseFindings(String aiResponse, String filePath) {
        List<CodeReviewFinding> findings = new ArrayList<>();
        if (aiResponse == null || aiResponse.trim().isEmpty()) return findings;

        String[] blocks = aiResponse.split("---");

        for (String block : blocks) {
            CodeReviewFinding finding = parseBlock(block, filePath);
            if (finding != null) {
                findings.add(finding);
            }
        }
        return findings;
    }

    private CodeReviewFinding parseBlock(String block, String filePath) {
        if (block.trim().isEmpty()) return null;
        
        CodeReviewFinding finding = new CodeReviewFinding();
        finding.setFilePath(filePath);
        
        String[] lines = block.trim().split("\n");
        for (String line : lines) {
            parseLine(line, finding);
        }
        
        return finding.getMessage() != null ? finding : null;
    }

    private void parseLine(String line, CodeReviewFinding finding) {
        if (line.startsWith("LINE:")) {
            parseLineField(line, finding);
        } else if (line.startsWith("SEVERITY:")) {
            finding.setSeverity(line.split(":")[1].trim());
        } else if (line.startsWith("CATEGORY:")) {
            finding.setCategory(line.split(":")[1].trim());
        } else if (line.startsWith("MESSAGE:")) {
            finding.setMessage(line.substring(line.indexOf(":") + 1).trim());
        }
    }

    private void parseLineField(String line, CodeReviewFinding finding) {
        try {
            finding.setLine(Integer.parseInt(line.split(":")[1].trim()));
        } catch (NumberFormatException e) {
            finding.setLine(0);
        }
    }

    public static class CodeReviewFinding {
        private String filePath;
        private int line;
        private String severity;
        private String category;
        private String message;

        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public int getLine() { return line; }
        public void setLine(int line) { this.line = line; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
