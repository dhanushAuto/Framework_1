package ai.generator;

import ai.service.AIService;
import java.util.List;
import java.util.Map;

public class TestCaseGenerator {

    private final AIService aiService = new AIService();

    public String generateTestCase(String requirement, String testType) throws Exception {
        String prompt = buildRequirementPrompt(requirement, testType);
        return aiService.ask(prompt);
    }

    public String generateTestData(String testCaseDescription, String dataType) throws Exception {
        String prompt = buildTestDataPrompt(testCaseDescription, dataType);
        return aiService.ask(prompt);
    }

    public String generateSeleniumCode(String pageDescription, List<String> userActions) throws Exception {
        String prompt = buildSeleniumPrompt(pageDescription, userActions);
        return aiService.ask(prompt);
    }

    public String generatePlaywrightCode(String pageDescription, List<String> userActions) throws Exception {
        String prompt = buildPlaywrightPrompt(pageDescription, userActions);
        return aiService.ask(prompt);
    }

    private String buildRequirementPrompt(String requirement, String testType) {
        return "Generate " + testType + " test case for: " + requirement;
    }

    private String buildTestDataPrompt(String description, String type) {
        return "Generate " + type + " test data for: " + description;
    }

    private String buildSeleniumPrompt(String desc, List<String> actions) {
        return "Generate Selenium code for: " + desc + "\nActions: " + String.join(", ", actions);
    }

    private String buildPlaywrightPrompt(String desc, List<String> actions) {
        return "Generate Playwright code for: " + desc + "\nActions: " + String.join(", ", actions);
    }

    public String generateApiTestsFromSwagger(String swaggerJson) throws Exception {
        String prompt = """
            You are a REST API Testing Expert.
            Generate comprehensive Rest Assured tests based on the following Swagger/OpenAPI definition.
            
            Include:
            1. Happy path tests.
            2. Negative tests (400, 401, 404, 500).
            3. Boundary tests for input parameters.
            4. Assertions for status code, headers, and body fields.
            
            Swagger JSON:
            %s
            
            Return ONLY the Java code for the tests.
            """.formatted(swaggerJson);
        return aiService.ask(prompt);
    }
}
