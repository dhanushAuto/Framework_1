package utilities.allure;

import io.qameta.allure.Attachment;
import org.openqa.selenium.WebDriver;
import utilities.ui.ScreenshotUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AllureAttachment {

    @Attachment(value = "Screenshot", type = "image/png")
    public static byte[] attachScreenshot(WebDriver driver) {
        try {
            ScreenshotUtils screenshotUtils = new ScreenshotUtils(driver);
            String screenshotPath = screenshotUtils.captureScreenshot("Allure_Screenshot");
            return Files.readAllBytes(Paths.get(screenshotPath));
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public static byte[] attachScreenshotOnFailure(WebDriver driver, String testName) {
        try {
            ScreenshotUtils screenshotUtils = new ScreenshotUtils(driver);
            String screenshotPath = screenshotUtils.captureScreenshot(testName + "_FAILURE");
            return Files.readAllBytes(Paths.get(screenshotPath));
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Attachment(value = "API Request", type = "application/json")
    public static String attachApiRequest(String requestDetails) {
        return requestDetails;
    }

    @Attachment(value = "API Response", type = "application/json")
    public static String attachApiResponse(String responseDetails) {
        return responseDetails;
    }

    @Attachment(value = "API Payload", type = "application/json")
    public static String attachApiPayload(String payload) {
        return payload;
    }

    @Attachment(value = "Log", type = "text/plain")
    public static String attachLog(String logMessage) {
        return logMessage;
    }

    @Attachment(value = "Text", type = "text/plain")
    public static String attachText(String text) {
        return text;
    }
}
