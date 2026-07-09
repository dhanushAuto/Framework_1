package utilities.AIUtils;

import ai.service.AIService;

public class AIUtils {

    private static final AIService ai = new AIService();

    public static String ask(String prompt) {
        try {
            return ai.ask(prompt);
        } catch (Exception e) {
            return "AI Error : " + e.getMessage();
        }
    }
}