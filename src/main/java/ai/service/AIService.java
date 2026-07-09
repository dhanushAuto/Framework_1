package ai.service;

import ai.client.AIClient;

public class AIService {

    private final AIClient client = new AIClient();

    public String ask(String prompt) throws Exception {

        return client.askAI(prompt);

    }

}
