package utilities;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class JavaScriptUtils {

    private final WebDriver driver;
    private final JavascriptExecutor js;

    public JavaScriptUtils(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
    }

    public void clickUsingJS(WebElement element) {
        js.executeScript("arguments[0].click();", element);
        log_utils.info("Clicked element using JS: " + element);
    }

    public void sendKeysUsingJS(WebElement element, String text) {
        js.executeScript("arguments[0].value=arguments[1];", element, text);
        log_utils.info("Sent keys using JS: " + text);
    }

    public void scrollToTop() {
        js.executeScript("window.scrollTo(0, 0);");
        log_utils.info("Scrolled to top of page");
    }

    public void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        log_utils.info("Scrolled to bottom of page");
    }

    public void scrollIntoView(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        log_utils.info("Scrolled element into view: " + element);
    }

    public void highlightElement(WebElement element) {
        js.executeScript(
                "arguments[0].style.border='3px solid red';", element);
        log_utils.info("Highlighted element: " + element);
    }

    public void drawBorder(WebElement element) {
        js.executeScript(
                "arguments[0].style.border='2px dashed blue';", element);
        log_utils.info("Drew border on element: " + element);
    }

    public void zoomPage(String zoomLevel) {
        js.executeScript("document.body.style.zoom=arguments[0];", zoomLevel);
        log_utils.info("Zoomed page to: " + zoomLevel);
    }

    public void refreshPage() {
        js.executeScript("location.reload();");
        log_utils.info("Refreshed page using JS");
    }

    public Long getPageHeight() {
        Long height = (Long) js.executeScript("return document.body.scrollHeight;");
        log_utils.info("Page height: " + height);
        return height;
    }

    public Long getPageWidth() {
        Long width = (Long) js.executeScript("return document.body.scrollWidth;");
        log_utils.info("Page width: " + width);
        return width;
    }
}
