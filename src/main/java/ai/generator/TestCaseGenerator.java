package ai.generator;

import ai.prompt.TestPromptBuilder;
import ai.service.AIService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestCaseGenerator {

    private final AIService aiService = new AIService();

    public GeneratedTest generateFromRequirement(String requirement, String testType) throws Exception {
        
        String prompt = TestPromptBuilder.buildTestCaseGenerationPrompt(requirement, testType);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseGeneratedTest(aiResponse);
    }

    public GeneratedTest generateFromAPI(String apiEndpoint, String apiMethod, Map<String, String> apiParams) throws Exception {
        
        String prompt = buildAPIPrompt(apiEndpoint, apiMethod, apiParams);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseGeneratedTest(aiResponse);
    }

    public GeneratedTest generateFromUI(String pageDescription, List<String> userActions) throws Exception {
        
        String prompt = buildUIPrompt(pageDescription, userActions);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseGeneratedTest(aiResponse);
    }

    public List<String> generateEdgeCases(String baseTestCase) throws Exception {
        
        String prompt = buildEdgeCasePrompt(baseTestCase);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseEdgeCases(aiResponse);
    }

    public String generateTestData(String testCaseDescription, String dataType) throws Exception {
        
        String prompt = buildTestDataPrompt(testCaseDescription, dataType);
        
        String aiResponse = aiService.ask(prompt);
        
        return aiResponse;
    }

    public String generateTestNGCode(String requirement, String testType) throws Exception {
        
        String prompt = TestPromptBuilder.buildTestCodeGenerationPrompt(requirement, testType, "TestNG");
        
        return aiService.ask(prompt);
    }

    public String generateRestAssuredCode(String apiEndpoint, String apiMethod, Map<String, String> apiParams) throws Exception {
        
        StringBuilder params = new StringBuilder();
        apiParams.forEach((key, value) -> 
            params.append(key).append(": ").append(value).append("\n")
        );
        
        String prompt = """
            You are a RestAssured Test Code Generation Expert.
            
            Generate executable RestAssured test code for the following API.
            
            API Endpoint: %s
            Method: %s
            
            Parameters:
            %s
            
            Provide complete, compilable Java code including:
            1. All necessary imports (io.restassured.*, org.testng.*, etc.)
            2. Test class with @Test annotation
            3. Request setup with given parameters
            4. Response validation assertions
            5. Proper error handling
            
            Return only the code, no explanations.
            """.formatted(apiEndpoint, apiMethod, params);
        
        return aiService.ask(prompt);
    }

    public String generateSeleniumCode(String pageDescription, List<String> userActions) throws Exception {
        
        String prompt = """
            You are a Selenium Test Code Generation Expert.
            
            Generate executable Selenium WebDriver test code for the following UI scenario.
            
            Page Description:
            %s
            
            User Actions:
            %s
            
            Provide complete, compilable Java code including:
            1. All necessary imports (org.openqa.selenium.*, org.testng.*, etc.)
            2. WebDriver setup and teardown
            3. Element locators using appropriate strategies
            4. Explicit waits for dynamic elements
            5. Assertions for validation
            6. Proper exception handling
            
            Return only the code, no explanations.
            """.formatted(pageDescription, String.join("\n", userActions));
        
        return aiService.ask(prompt);
    }

    public String generatePlaywrightCode(String pageDescription, List<String> userActions) throws Exception {
        
        String prompt = """
            You are a Playwright Test Code Generation Expert.
            
            Generate executable Playwright test code for the following UI scenario.
            
            Page Description:
            %s
            
            User Actions:
            %s
            
            Provide complete, compilable Java code including:
            1. All necessary imports (com.microsoft.playwright.*, org.testng.*, etc.)
            2. Playwright setup and teardown
            3. Element selectors using best practices
            4. Auto-waiting for dynamic elements
            5. Assertions for validation
            6. Proper error handling
            
            Return only the code, no explanations.
            """.formatted(pageDescription, String.join("\n", userActions));
        
        return aiService.ask(prompt);
    }

    private String buildRequirementPrompt(String requirement, String testType) {
        return """
            You are a Test Case Generation Expert.
            
            Generate a comprehensive test case from the following requirement.
            
            Requirement:
            %s
            
            Test Type: %s (API/UI/Performance/Security)
            
            Provide the test case in the following format:
            
            TEST_NAME: [Descriptive test name]
            DESCRIPTION: [Test description]
            PRECONDITIONS: [List of preconditions]
            TEST_STEPS: [Numbered list of steps]
            EXPECTED_RESULT: [Expected outcome]
            TEST_DATA: [Required test data]
            PRIORITY: [HIGH/MEDIUM/LOW]
            TAGS: [Comma-separated tags]
            """.formatted(requirement, testType);
    }

    private String buildAPIPrompt(String apiEndpoint, String apiMethod, Map<String, String> apiParams) {
        StringBuilder params = new StringBuilder();
        apiParams.forEach((key, value) -> 
            params.append(key).append(": ").append(value).append("\n")
        );
        
        return """
            You are an API Test Case Generation Expert.
            
            Generate comprehensive API test cases for the following endpoint.
            
            API Endpoint: %s
            Method: %s
            
            Parameters:
            %s
            
            Generate test cases for:
            1. Happy path
            2. Invalid parameters
            3. Missing parameters
            4. Boundary values
            5. Error handling
            
            Format each test case as:
            TEST_NAME: [Name]
            DESCRIPTION: [Description]
            REQUEST: [Request details]
            EXPECTED_STATUS: [HTTP status code]
            EXPECTED_RESPONSE: [Expected response structure]
            """.formatted(apiEndpoint, apiMethod, params);
    }

    private String buildUIPrompt(String pageDescription, List<String> userActions) {
        return """
            You are a UI Test Case Generation Expert.
            
            Generate comprehensive UI test cases for the following page and actions.
            
            Page Description:
            %s
            
            User Actions:
            %s
            
            Generate test cases covering:
            1. Functional testing
            2. UI validation
            3. User flow testing
            4. Responsive design
            5. Accessibility
            
            Format each test case as:
            TEST_NAME: [Name]
            DESCRIPTION: [Description]
            LOCATORS: [Element locators]
            STEPS: [Numbered steps]
            VALIDATIONS: [Assertions]
            """.formatted(pageDescription, String.join("\n", userActions));
    }

    private String buildEdgeCasePrompt(String baseTestCase) {
        return """
            You are a Test Case Generation Expert specializing in edge cases.
            
            Generate edge case scenarios for the following base test case.
            
            Base Test Case:
            %s
            
            Provide edge cases for:
            1. Boundary values
            2. Null/empty values
            3. Invalid data types
            4. Extreme values
            5. Concurrent operations
            6. Error conditions
            
            Return a comma-separated list of edge case descriptions.
            """.formatted(baseTestCase);
    }

    private String buildTestDataPrompt(String testCaseDescription, String dataType) {
        return """
            You are a Test Data Generation Expert.
            
            Generate appropriate test data for the following test case.
            
            Test Case: %s
            Data Type: %s
            
            Provide:
            1. Valid data samples
            2. Invalid data samples
            3. Boundary data samples
            4. Format: JSON or key-value pairs
            """.formatted(testCaseDescription, dataType);
    }

    private GeneratedTest parseGeneratedTest(String aiResponse) {
        GeneratedTest test = new GeneratedTest();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("TEST_NAME:")) {
                test.setTestName(line.split(":", 2)[1].trim());
            } else if (line.startsWith("DESCRIPTION:")) {
                test.setDescription(line.split(":", 2)[1].trim());
            } else if (line.startsWith("PRECONDITIONS:")) {
                test.setPreconditions(line.split(":", 2)[1].trim());
            } else if (line.startsWith("TEST_STEPS:")) {
                test.setTestSteps(line.split(":", 2)[1].trim());
            } else if (line.startsWith("EXPECTED_RESULT:")) {
                test.setExpectedResult(line.split(":", 2)[1].trim());
            } else if (line.startsWith("TEST_DATA:")) {
                test.setTestData(line.split(":", 2)[1].trim());
            } else if (line.startsWith("PRIORITY:")) {
                test.setPriority(line.split(":", 2)[1].trim());
            } else if (line.startsWith("TAGS:")) {
                test.setTags(line.split(":", 2)[1].trim());
            }
        }
        
        return test;
    }

    private List<String> parseEdgeCases(String aiResponse) {
        String[] cases = aiResponse.split(",");
        List<String> result = new ArrayList<>();
        for (String testCase : cases) {
            result.add(testCase.trim());
        }
        return result;
    }

    public static class GeneratedTest {
        private String testName;
        private String description;
        private String preconditions;
        private String testSteps;
        private String expectedResult;
        private String testData;
        private String priority = "MEDIUM";
        private String tags;

        public String getTestName() { return testName; }
        public void setTestName(String testName) { this.testName = testName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getPreconditions() { return preconditions; }
        public void setPreconditions(String preconditions) { this.preconditions = preconditions; }
        
        public String getTestSteps() { return testSteps; }
        public void setTestSteps(String testSteps) { this.testSteps = testSteps; }
        
        public String getExpectedResult() { return expectedResult; }
        public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
        
        public String getTestData() { return testData; }
        public void setTestData(String testData) { this.testData = testData; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
    }
}
