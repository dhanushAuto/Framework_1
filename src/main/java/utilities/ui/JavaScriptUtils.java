package utilities.ui;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.common_utils.LogUtils;

public class JavaScriptUtils {

    private final JavascriptExecutor js;

    public JavaScriptUtils(WebDriver driver) {
        this.js = (JavascriptExecutor) driver;
    }

    public void clickUsingJS(WebElement element) {
        js.executeScript("arguments[0].click();", element);
        LogUtils.info("Clicked element using JS: " + element);
    }

    public void sendKeysUsingJS(WebElement element, String text) {
        js.executeScript("arguments[0].value=arguments[1];", element, text);
        LogUtils.info("Sent keys using JS: " + text);
    }

    public void scrollToTop() {
        js.executeScript("window.scrollTo(0, 0);");
        LogUtils.info("Scrolled to top of page");
    }

    public void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        LogUtils.info("Scrolled to bottom of page");
    }

    public void scrollIntoView(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        LogUtils.info("Scrolled element into view: " + element);
    }

    public void highlightElement(WebElement element) {
        js.executeScript(
                "arguments[0].style.border='3px solid red';", element);
        LogUtils.info("Highlighted element: " + element);
    }

    public void drawBorder(WebElement element) {
        js.executeScript(
                "arguments[0].style.border='2px dashed blue';", element);
        LogUtils.info("Drew border on element: " + element);
    }

    public void zoomPage(String zoomLevel) {
        js.executeScript("document.body.style.zoom=arguments[0];", zoomLevel);
        LogUtils.info("Zoomed page to: " + zoomLevel);
    }

    public void refreshPage() {
        js.executeScript("location.reload();");
        LogUtils.info("Refreshed page using JS");
    }

    public Long getPageHeight() {
        Long height = (Long) js.executeScript("return document.body.scrollHeight;");
        LogUtils.info("Page height: " + height);
        return height;
    }

    public Long getPageWidth() {
        Long width = (Long) js.executeScript("return document.body.scrollWidth;");
        LogUtils.info("Page width: " + width);
        return width;
    }
}
