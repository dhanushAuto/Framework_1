package ai.generator;

import ai.client.AIClient;
import ai.model.OllamaResponse;
import ai.model.SonarFix;
import ai.model.SonarIssue;
import ai.prompt.SonarPromptBuilder;
import ai.service.CodeExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import utilities.AIUtils.CacheUtils;
import utilities.common_utils.FileUtils;
import utilities.common_utils.PatchUtils;
import utilities.common_utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarFixGenerator {

    private final AIClient client = new AIClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, String> commonFixCache = new HashMap<>();

    public List<SonarFix> processFileIssues(List<SonarIssue> issues) {
        if (issues == null || issues.isEmpty()) return new ArrayList<>();

        String filePath = issues.get(0).getFilePath();
        LogUtils.info("[" + Thread.currentThread().getName() + "] Processing: " + filePath);

        List<SonarFix> results = new ArrayList<>();
        List<SonarIssue> issuesToAi = new ArrayList<>();

        for (SonarIssue issue : issues) {
            String cacheKey = issue.getRule().replace(":", "_");
            try {
                if (CacheUtils.exists(cacheKey)) {
                    LogUtils.info("Loaded from cache: " + cacheKey);
                    SonarFix fix = mapper.readValue(CacheUtils.read(cacheKey), SonarFix.class);
                    fix.setIssue(issue);
                    
                    // Update start/end lines for this specific file
                    int[] range = CodeExtractor.getMethodRange(FileUtils.getLocalFilePath(issue.getComponent()), issue.getLine());
                    if (range != null) {
                        fix.setStartLine(range[0]);
                        fix.setEndLine(range[1]);
                    }
                    
                    results.add(fix);
                    continue;
                }
                
                if (commonFixCache.containsKey(issue.getRule())) {
                    SonarFix fix = new SonarFix();
                    fix.setIssue(issue);
                    fix.setFixedCode(commonFixCache.get(issue.getRule()));
                    fix.setFixed(true);
                    
                    int[] range = CodeExtractor.getMethodRange(FileUtils.getLocalFilePath(issue.getComponent()), issue.getLine());
                    if (range != null) {
                        fix.setStartLine(range[0]);
                        fix.setEndLine(range[1]);
                    }
                    
                    results.add(fix);
                    continue;
                }
            } catch (Exception e) {
                LogUtils.error("Cache error: " + e.getMessage());
            }

            issuesToAi.add(issue);
        }

        if (!issuesToAi.isEmpty()) {
            results.addAll(generateBatchFixes(issuesToAi));
        }

        return results;
    }

    private List<SonarFix> generateBatchFixes(List<SonarIssue> issues) {
        List<SonarFix> fixes = new ArrayList<>();
        
        for (SonarIssue issue : issues) {
            try {
                String localPath = FileUtils.getLocalFilePath(issue.getComponent());
                String sourceCode = CodeExtractor.extractCode(localPath, issue.getLine());
                int[] range = CodeExtractor.getMethodRange(localPath, issue.getLine());

                String prompt = SonarPromptBuilder.buildFixPrompt(issue, sourceCode);
                OllamaResponse response = client.generateFullResponse(prompt);
                
                if (response != null && response.getResponse() != null) {
                    SonarFix fix = new SonarFix();
                    fix.setIssue(issue);
                    fix.setAiExplanation("Generated using AI");
                    fix.setFixedCode(extractCodeFromResponse(response.getResponse()));
                    fix.setFixed(true);
                    
                    if (range != null) {
                        fix.setStartLine(range[0]);
                        fix.setEndLine(range[1]);
                    }

                    // Cache it
                    String cacheKey = issue.getRule().replace(":", "_");
                    CacheUtils.write(cacheKey, mapper.writeValueAsString(fix));
                    commonFixCache.put(issue.getRule(), fix.getFixedCode());
                    
                    // Patch generation
                    String patchPath = PatchUtils.generatePatch(localPath, sourceCode, fix.getFixedCode());
                    fix.setPatchPath(patchPath);

                    fixes.add(fix);
                }
            } catch (Exception e) {
                LogUtils.error("AI Fix Generation failed for " + issue.getRule() + ": " + e.getMessage());
                SonarFix fail = new SonarFix();
                fail.setIssue(issue);
                fail.setFixed(false);
                fail.setAiExplanation("Error: " + e.getMessage());
                fixes.add(fail);
            }
        }
        
        return fixes;
    }

    private String extractCodeFromResponse(String response) {
        if (response.contains("```java")) {
            return response.substring(response.indexOf("```java") + 7, response.lastIndexOf("```"));
        } else if (response.contains("```")) {
            return response.substring(response.indexOf("```") + 3, response.lastIndexOf("```"));
        }
        return response;
    }
}
