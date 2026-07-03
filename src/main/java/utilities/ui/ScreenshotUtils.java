package utilities.ui;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.common_utils.log_utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "test-output/screenshots/";
    private final WebDriver driver;

    public ScreenshotUtils(WebDriver driver) {
        this.driver = driver;
    }

    private String timestampedName(String baseName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return baseName + "_" + timestamp + ".png";
    }

    public String captureScreenshot(String name) {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path = SCREENSHOT_DIR + timestampedName(name);
        try {
            File dest = new File(path);
            FileUtils.copyFile(src, dest);
            log_utils.info("Captured screenshot: " + path);
        } catch (IOException e) {
            log_utils.error("Failed to capture screenshot: " + e.getMessage());
        }
        return path;
    }

    public String captureElementScreenshot(WebElement element, String name) {
        File src = element.getScreenshotAs(OutputType.FILE);
        String path = SCREENSHOT_DIR + timestampedName(name);
        try {
            File dest = new File(path);
            FileUtils.copyFile(src, dest);
            log_utils.info("Captured element screenshot: " + path);
        } catch (IOException e) {
            log_utils.error("Failed to capture element screenshot: " + e.getMessage());
        }
        return path;
    }

    public String captureFullPageScreenshot(String name) {
        // Requires a full-page capable driver setup (e.g. Selenium 4 Firefox
        // full-page screenshot API, or a plugin like AShot for Chrome stitching).
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path = SCREENSHOT_DIR + timestampedName(name + "_fullpage");
        try {
            File dest = new File(path);
            FileUtils.copyFile(src, dest);
            log_utils.info("Captured full-page screenshot: " + path);
        } catch (IOException e) {
            log_utils.error("Failed to capture full-page screenshot: " + e.getMessage());
        }
        return path;
    }

    public String getScreenshotPath(String fileName) {
        return SCREENSHOT_DIR + fileName;
    }
}


