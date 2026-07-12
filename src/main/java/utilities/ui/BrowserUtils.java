package utilities.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import utilities.common_utils.LogUtils;

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
        LogUtils.info("Launched browser: " + browserName);
        return driver;
    }
    public static String getBrowser() {
        return browser;
    }
    public void maximize() {
        driver.manage().window().maximize();
        LogUtils.info("Window maximized");
    }

    public void minimize() {
        driver.manage().window().minimize();
        LogUtils.info("Window minimized");
    }

    public void fullscreen() {
        driver.manage().window().fullscreen();
        LogUtils.info("Window set to fullscreen");
    }

    public void refresh() {
        driver.navigate().refresh();
        LogUtils.info("Page refreshed");
    }

    public void back() {
        driver.navigate().back();
        LogUtils.info("Navigated back");
    }

    public void forward() {
        driver.navigate().forward();
        LogUtils.info("Navigated forward");
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);
        LogUtils.info("Navigated to URL: " + url);
    }

    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        LogUtils.info("Current URL: " + url);
        return url;
    }

    public String getTitle() {
        String title = driver.getTitle();
        LogUtils.info("Page title: " + title);
        return title;
    }

    public void closeBrowser() {
        if (driver != null) {
            driver.close();
            LogUtils.info("Closed current browser window");
        }
    }

    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
            LogUtils.info("Quit browser session");
        }
    }
}

