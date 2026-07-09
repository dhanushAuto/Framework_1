package ai.prompt;

public class HealingPromptBuilder {

    public static String buildSelfHealingPrompt(
            String originalLocator,
            String elementDescription,
            String pageContext,
            String errorMessage) {

        return """
            You are a Test Automation Healing Expert.
            
            The following element locator failed during test execution. Analyze the failure and suggest a healed locator.
            
            Original Locator: %s
            Element Description: %s
            Page Context: %s
            Error Message: %s
            
            Provide a healed locator in the format: By.<strategy>("<value>")
            Strategies: id, name, xpath, cssSelector, className, tagName, linkText, partialLinkText
            
            Return only the locator string, nothing else.
            """.formatted(originalLocator, elementDescription, pageContext, errorMessage);
    }

    public static String buildElementHealingPrompt(String elementDescription, String pageContext) {
        return """
            You are a Test Automation Healing Expert.
            
            An element interaction failed. Suggest alternative strategies to interact with this element.
            
            Element Description: %s
            Page Context: %s
            
            Suggest healing strategies:
            1. Wait strategies
            2. Alternative locators
            3. JavaScript execution
            4. Action class usage
            
            Provide specific code snippets for each strategy.
            """.formatted(elementDescription, pageContext);
    }

    public static String buildAssertionHealingPrompt(String expectedValue, String actualValue, String assertionContext) {
        return """
            You are a Test Automation Healing Expert.
            
            An assertion failed. Analyze if this is a data mismatch, timing issue, or actual defect.
            
            Expected Value: %s
            Actual Value: %s
            Assertion Context: %s
            
            Provide:
            1. Analysis of the mismatch
            2. Suggested healing approach (wait, retry, data normalization, or actual defect)
            3. Corrected assertion if applicable
            """.formatted(expectedValue, actualValue, assertionContext);
    }
}
