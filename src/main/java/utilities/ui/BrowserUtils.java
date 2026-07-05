package utilities.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.Browser;
import utilities.common_utils.log_utils;

public class BrowserUtils {

    private WebDriver driver;
    private static String browser;

    public WebDriver launchBrowser(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
        log_utils.info("Launched browser: " + browserName);
        return driver;
    }
    public static String getBrowser() {
        return browser;
    }
    public void maximize() {
        driver.manage().window().maximize();
        log_utils.info("Window maximized");
    }

    public void minimize() {
        driver.manage().window().minimize();
        log_utils.info("Window minimized");
    }

    public void fullscreen() {
        driver.manage().window().fullscreen();
        log_utils.info("Window set to fullscreen");
    }

    public void refresh() {
        driver.navigate().refresh();
        log_utils.info("Page refreshed");
    }

    public void back() {
        driver.navigate().back();
        log_utils.info("Navigated back");
    }

    public void forward() {
        driver.navigate().forward();
        log_utils.info("Navigated forward");
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);
        log_utils.info("Navigated to URL: " + url);
    }

    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        log_utils.info("Current URL: " + url);
        return url;
    }

    public String getTitle() {
        String title = driver.getTitle();
        log_utils.info("Page title: " + title);
        return title;
    }

    public void closeBrowser() {
        if (driver != null) {
            driver.close();
            log_utils.info("Closed current browser window");
        }
    }

    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
            log_utils.info("Quit browser session");
        }
    }
}

