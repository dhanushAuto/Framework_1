package ai.prompt;

public class TestPromptBuilder {

    public static String buildTestImpactPrompt(String changedFiles, String allTests) {
        return """
            You are a Test Impact Analysis Expert.
            
            Analyze the impact of the following code changes on the test suite.
            
            Changed Files:
            %s
            
            Available Tests:
            %s
            
            Provide:
            1. List of tests that MUST be run (high impact)
            2. List of tests that SHOULD be run (medium impact)
            3. List of tests that CAN BE SKIPPED (low impact)
            4. Risk level (HIGH/MEDIUM/LOW)
            
            Format your response as:
            HIGH_IMPACT: test1, test2, test3
            MEDIUM_IMPACT: test4, test5
            LOW_IMPACT: test6, test7
            RISK_LEVEL: HIGH
            """.formatted(changedFiles, allTests);
    }

    public static String buildTestCaseGenerationPrompt(String requirement, String testType) {
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

    public static String buildTestCodeGenerationPrompt(String requirement, String testType, String framework) {
        return """
            You are a Test Code Generation Expert.
            
            Generate executable test code from the following requirement.
            
            Requirement:
            %s
            
            Test Type: %s
            Framework: %s (TestNG/RestAssured/Selenium/Playwright)
            
            Provide complete, compilable test code including:
            1. All necessary imports
            2. Test class structure
            3. Test methods with annotations
            4. Assertions
            5. Setup and teardown methods
            
            Return only the code, no explanations.
            """.formatted(requirement, testType, framework);
    }

    public static String buildTraceabilityPrompt(String requirements, String testCases) {
        return """
            You are a Requirements Traceability Expert.
            
            Create a traceability matrix mapping requirements to test cases.
            
            Requirements:
            %s
            
            Available Test Cases:
            %s
            
            For each requirement, identify which test cases cover it.
            
            Format:
            REQ-001: test1, test2, test3
            REQ-002: test4, test5
            REQ-003: test1, test6
            """.formatted(requirements, testCases);
    }
}
