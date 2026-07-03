package utilities.ui;

import org.openqa.selenium.WebDriver;
import utilities.common_utils.log_utils;

import java.util.Set;

public class WindowUtils {

    private final WebDriver driver;
    private String parentWindowHandle;

    public WindowUtils(WebDriver driver) {
        this.driver = driver;
        this.parentWindowHandle = driver.getWindowHandle();
    }

    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
        log_utils.info("Switched to window: " + windowHandle);
    }

    public void switchToNewWindow() {
        String current = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(current)) {
                driver.switchTo().window(handle);
                log_utils.info("Switched to new window: " + handle);
                return;
            }
        }
        log_utils.warn("No new window found to switch to");
    }

    public void switchToParentWindow() {
        driver.switchTo().window(parentWindowHandle);
        log_utils.info("Switched to parent window: " + parentWindowHandle);
    }

    public void closeCurrentWindow() {
        driver.close();
        log_utils.info("Closed current window");
    }

    public void closeChildWindows() {
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(parentWindowHandle)) {
                driver.switchTo().window(handle);
                driver.close();
                log_utils.info("Closed child window: " + handle);
            }
        }
        driver.switchTo().window(parentWindowHandle);
    }

    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    public String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }
}


