package utilities.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import utilities.common_utils.log_utils;

public class ActionUtils {

    private WebDriver driver;
    private Actions actions;

    public ActionUtils(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
    }

    public void click(WebElement element) {
        actions.moveToElement(element).click().perform();
        log_utils.info("Clicked on element: " + element);
    }

    public void doubleClick(WebElement element) {
        actions.moveToElement(element).doubleClick().perform();
        log_utils.info("Double-clicked on element: " + element);
    }

    public void rightClick(WebElement element) {
        actions.contextClick(element).perform();
        log_utils.info("Right-clicked on element: " + element);
    }

    public void hover(WebElement element) {
        actions.moveToElement(element).perform();
        log_utils.info("Hovered on element: " + element);
    }

    public void dragAndDrop(WebElement source, WebElement target) {
        actions.dragAndDrop(source, target).perform();
        log_utils.info("Dragged element " + source + " to " + target);
    }

    public void clickAndHold(WebElement element) {
        actions.clickAndHold(element).perform();
        log_utils.info("Clicked and held element: " + element);
    }

    public void release() {
        actions.release().perform();
        log_utils.info("Released mouse button");
    }

    public void moveToElement(WebElement element) {
        actions.moveToElement(element).perform();
        log_utils.info("Moved to element: " + element);
    }

    public void scrollToElement(WebElement element) {
        actions.scrollToElement(element).perform();
        log_utils.info("Scrolled to element: " + element);
    }

    public void sendKeysUsingActions(WebElement element, CharSequence... keys) {
        actions.moveToElement(element).sendKeys(keys).perform();
        log_utils.info("Sent keys using Actions to element: " + element);
    }
}



