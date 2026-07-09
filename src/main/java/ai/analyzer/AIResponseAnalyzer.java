package ai.analyzer;

import ai.service.AIService;
import io.restassured.response.Response;
import utilities.api.config_utils;

public class AIResponseAnalyzer {

    private static final AIService aiService = new AIService();

    /**
     * Analyze API response using AI.
     *
     * @param response RestAssured Response
     * @return AI Analysis
     */
    public static String analyze(Response response) {

        try {
            config_utils.loadProperties();
            String prompt = """
                    Analyze the following REST API response.

                    Give the answer in the below format:

                    1. Summary
                    2. Is the API successful?
                    3. Any issues found?
                    4. Possible improvements
                    5. Final Recommendation

                    HTTP Status Code:
                    %d

                    Response Body:
                    %s
                    """
                    .formatted(
                            response.getStatusCode(),
                            response.asPrettyString()
                    );

            return aiService.ask(prompt);

        } catch (Exception e) {

            return "AI Analysis Failed : " + e.getMessage();

        }

    }

}