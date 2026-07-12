package utilities.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import utilities.common_utils.LogUtils;

public class ActionUtils {

    private Actions actions;

    public ActionUtils(WebDriver driver) {
        this.actions = new Actions(driver);
    }

    public void click(WebElement element) {
        actions.moveToElement(element).click().perform();
        LogUtils.info("Clicked on element: " + element);
    }

    public void doubleClick(WebElement element) {
        actions.moveToElement(element).doubleClick().perform();
        LogUtils.info("Double-clicked on element: " + element);
    }

    public void rightClick(WebElement element) {
        actions.contextClick(element).perform();
        LogUtils.info("Right-clicked on element: " + element);
    }

    public void hover(WebElement element) {
        actions.moveToElement(element).perform();
        LogUtils.info("Hovered on element: " + element);
    }

    public void dragAndDrop(WebElement source, WebElement target) {
        actions.dragAndDrop(source, target).perform();
        LogUtils.info("Dragged element " + source + " to " + target);
    }

    public void clickAndHold(WebElement element) {
        actions.clickAndHold(element).perform();
        LogUtils.info("Clicked and held element: " + element);
    }

    public void release() {
        actions.release().perform();
        LogUtils.info("Released mouse button");
    }

    public void moveToElement(WebElement element) {
        actions.moveToElement(element).perform();
        LogUtils.info("Moved to element: " + element);
    }

    public void scrollToElement(WebElement element) {
        actions.scrollToElement(element).perform();
        LogUtils.info("Scrolled to element: " + element);
    }

    public void sendKeysUsingActions(WebElement element, CharSequence... keys) {
        actions.moveToElement(element).sendKeys(keys).perform();
        LogUtils.info("Sent keys using Actions to element: " + element);
    }
}



