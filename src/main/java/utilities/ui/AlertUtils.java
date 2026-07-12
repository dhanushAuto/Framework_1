package utilities.ui;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import utilities.common_utils.LogUtils;


public class AlertUtils {

    private final WebDriver driver;

    public AlertUtils(WebDriver driver) {
        this.driver = driver;
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
        LogUtils.info("Accepted alert");
    }

    public void dismissAlert() {
        driver.switchTo().alert().dismiss();
        LogUtils.info("Dismissed alert");
    }

    public String getAlertText() {
        String text = driver.switchTo().alert().getText();
        LogUtils.info("Alert text: " + text);
        return text;
    }

    public void sendKeysToAlert(String text) {
        driver.switchTo().alert().sendKeys(text);
        LogUtils.info("Sent keys to alert: " + text);
    }

    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }
}



