package ai.analyzer;

import ai.service.AIService;
import io.restassured.response.Response;

public class APIFailureAnalyzer {

    private static final AIService aiService = new AIService();

    public static String analyze(Response response, int expectedStatusCode) {

        try {

            String prompt = """
                    You are an API Automation Expert.

                    Analyze why this API test failed.

                    Expected HTTP Status Code:
                    %d

                    Actual HTTP Status Code:
                    %d

                    Response Body:
                    %s

                    Give the answer in this format only:

                    1. Failure Summary
                    2. Possible Root Cause
                    3. Suggested Fix
                    4. Recommendation

                    Keep the explanation within 8-10 lines.
                    """
                    .formatted(
                            expectedStatusCode,
                            response.getStatusCode(),
                            response.asPrettyString());

            return aiService.ask(prompt);

        } catch (Exception e) {

            return "AI Failure Analysis Failed : " + e.getMessage();

        }

    }
}