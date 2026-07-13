package ai.service;

import ai.client.AIClient;
import ai.model.OllamaResponse;
import java.util.ArrayList;
import java.util.List;

public class AIChatAssistant {

    private final AIClient client = new AIClient();
    private final List<ChatMessage> history = new ArrayList<>();
    private String context = "";

    public void setContext(String context) {
        this.context = context;
    }

    public String ask(String userMessage) {
        history.add(new ChatMessage("user", userMessage));
        
        String prompt = buildPrompt();
        OllamaResponse response = client.generateFullResponse(prompt);
        
        String aiMessage = response.getResponse();
        history.add(new ChatMessage("assistant", aiMessage));
        
        return aiMessage;
    }

    private String buildPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a helpful AI assistant for an Automation Testing Framework.\n");
        sb.append("The framework uses Selenium, TestNG, API testing, and SonarQube integration.\n");
        
        if (!context.isEmpty()) {
            sb.append("\nCURRENT CONTEXT:\n").append(context).append("\n");
        }
        
        sb.append("\nCONVERSATION HISTORY:\n");
        for (ChatMessage msg : history) {
            sb.append(msg.role.toUpperCase()).append(": ").append(msg.content).append("\n");
        }
        
        sb.append("\nASSISTANT: ");
        return sb.toString();
    }

    public void clearHistory() {
        history.clear();
    }

    private static class ChatMessage {
        String role;
        String content;

        ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
