package ai.healing;

import ai.service.AIService;
import drivermanager.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.common_utils.LogUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelfHealingEngine {

    private final AIService aiService = new AIService();
    private WebDriver driver;

    public SelfHealingEngine() {
        this.driver = Driver.getDriver();
    }

    public SelfHealingEngine(WebDriver driver) {
        this.driver = driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public By healAndUpdate(String elementDescription, String pageContext, String errorMessage, String filePath) throws Exception {
        LogUtils.info("Starting self-healing for: " + elementDescription);
        String pageSource = "";
        if (driver != null) {
            try {
                pageSource = driver.getPageSource();
            } catch (Exception e) {
                LogUtils.warn("Could not get page source: " + e.getMessage());
            }
        }
        String prompt = buildHealingPromptWithSource(elementDescription, pageContext, errorMessage, pageSource);
        String aiResponse = aiService.ask(prompt);
        By newLocator = parseHealedLocator(aiResponse);

        if (newLocator != null && filePath != null) {
            updateLocatorInFile(filePath, elementDescription, aiResponse.trim());
        }

        return newLocator;
    }

    private String buildHealingPromptWithSource(String elementDescription, String pageContext, String errorMessage, String pageSource) {
        String safeSource = (pageSource != null) ? pageSource : "";
        return """
            You are a Selenium Self-Healing Expert.
            The element described as '%s' failed to be located on page '%s'.
            Error: %s
            
            Current Page Source (truncated):
            %s
            
            Suggest a new robust Selenium locator (id, name, css, or xpath).
            Return ONLY the locator string in format: type=value (e.g., xpath=//button[@id='login'])
            """.formatted(elementDescription, pageContext, errorMessage, 
                safeSource.substring(0, Math.min(safeSource.length(), 2000)));
    }

    private void updateLocatorInFile(String filePath, String elementDescription, String newLocator) {
        try {
            Path path = Path.of(filePath);
            String content = Files.readString(path);
            
            // Heuristic to find the variable name or description in the file
            // This is a simplified version; in a real scenario, we might use a more robust AST-based approach
            String patternString = "(?i)By\\s+\\w+\\s*=\\s*By\\.\\w+\\(.*?\\);\\s*//\\s*" + Pattern.quote(elementDescription);
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(content);
            
            if (matcher.find()) {
                String oldLine = matcher.group();
                String locatorType = newLocator.split("=")[0];
                String locatorValue = newLocator.split("=")[1];
                
                String newBy;
                if (locatorType.equalsIgnoreCase("xpath")) newBy = "By.xpath(\"" + locatorValue + "\")";
                else if (locatorType.equalsIgnoreCase("id")) newBy = "By.id(\"" + locatorValue + "\")";
                else if (locatorType.equalsIgnoreCase("css")) newBy = "By.cssSelector(\"" + locatorValue + "\")";
                else if (locatorType.equalsIgnoreCase("name")) newBy = "By.name(\"" + locatorValue + "\")";
                else newBy = "By.cssSelector(\"" + locatorValue + "\")";
                
                String newLine = oldLine.replaceAll("By\\.\\w+\\(.*?\\)", newBy);
                String newContent = content.replace(oldLine, newLine);
                Files.writeString(path, newContent);
                LogUtils.info("Automatically updated locator in file: " + filePath);
            }
        } catch (IOException e) {
            LogUtils.error("Failed to update locator in file: " + e.getMessage());
        }
    }

    private By parseHealedLocator(String aiResponse) {
        String locator = aiResponse.trim();
        if (locator.startsWith("xpath=")) return By.xpath(locator.substring(6));
        if (locator.startsWith("//")) return By.xpath(locator);
        if (locator.startsWith("id=")) return By.id(locator.substring(3));
        if (locator.startsWith("name=")) return By.name(locator.substring(5));
        if (locator.startsWith("css=")) return By.cssSelector(locator.substring(4));
        return By.cssSelector(locator);
    }

    public List<By> getFallbackLocators(String elementDescription) {
        List<By> fallbacks = new ArrayList<>();
        String lowerDesc = elementDescription.toLowerCase();
        if (lowerDesc.contains("button") || lowerDesc.contains("submit")) {
            fallbacks.add(By.xpath("//button[contains(text(), '" + elementDescription + "')]"));
            fallbacks.add(By.cssSelector("button[type='submit']"));
        }
        if (lowerDesc.contains("input") || lowerDesc.contains("field")) {
            fallbacks.add(By.xpath("//input[@placeholder='" + elementDescription + "']"));
        }
        return fallbacks;
    }
}
