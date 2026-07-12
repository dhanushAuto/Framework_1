package utilities.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.common_utils.LogUtils;

public class FrameUtils {

    private final WebDriver driver;

    public FrameUtils(WebDriver driver) {
        this.driver = driver;
    }

    public void switchToFrame(int index) {
        driver.switchTo().frame(index);
        LogUtils.info("Switched to frame by index: " + index);
    }

    public void switchToFrame(String nameOrId) {
        driver.switchTo().frame(nameOrId);
        LogUtils.info("Switched to frame by name/id: " + nameOrId);
    }

    public void switchToFrame(WebElement frameElement) {
        driver.switchTo().frame(frameElement);
        LogUtils.info("Switched to frame by WebElement: " + frameElement);
    }

    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
        LogUtils.info("Switched to parent frame");
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        LogUtils.info("Switched to default content");
    }
}

