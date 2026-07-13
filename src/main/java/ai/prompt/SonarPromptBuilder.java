package ai.prompt;

import ai.model.SonarIssue;
import java.util.List;

public final class SonarPromptBuilder {

    private SonarPromptBuilder() {}

    public static String buildFixPrompt(SonarIssue issue, String sourceCode) {
        return """
            You are a Senior Java Developer.
            Fix the following SonarQube issue:
            
            RULE: %s
            SEVERITY: %s
            MESSAGE: %s
            
            SOURCE CODE:
            %s
            
            Provide ONLY the fixed Java code within triple backticks (```java ... ```).
            Do not include any explanation outside the code block.
            """.formatted(issue.getRule(), issue.getSeverity(), issue.getMessage(), sourceCode);
    }

    public static String buildBatchPrompt(List<SonarIssue> issues, String sourceCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a Senior Java Developer. Fix the following SonarQube issues in this code snippet:\n\n");
        
        for (SonarIssue issue : issues) {
            sb.append("RULE: ").append(issue.getRule()).append("\n");
            sb.append("MESSAGE: ").append(issue.getMessage()).append("\n\n");
        }
        
        sb.append("SOURCE CODE:\n").append(sourceCode).append("\n\n");
        sb.append("Provide ONLY the fixed Java code within triple backticks (```java ... ```).");
        
        return sb.toString();
    }
}
