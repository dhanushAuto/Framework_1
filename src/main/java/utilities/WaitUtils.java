package utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.NoSuchElementException;

public class WaitUtils {


    private final WebDriver driver;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
    }

    public void implicitWait(int seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
        log_utils.info("Set implicit wait: " + seconds + "s");
    }

    public WebDriverWait explicitWait(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    public WebElement waitForVisibility(WebElement element) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForClickable(WebElement element) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement waitForPresence(By locator) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public boolean waitForInvisibility(WebElement element) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOf(element));
    }

    public boolean waitForText(WebElement element, String text) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public boolean waitForTitle(String title) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.titleIs(title));
    }

    public boolean waitForUrl(String url) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.urlToBe(url));
    }

    public WebElement fluentWait(By locator, int timeoutSeconds, int pollingMillis) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis))
                .ignoring(NoSuchElementException.class);

        return wait.until(d -> d.findElement(locator));
    }
}

