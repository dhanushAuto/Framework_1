package DriverManager;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

import java.time.Duration;

public class driver {

    public static void initDriver(String browser) {
         WebDriverManager.chromedriver().setup();
    }
    public static WebDriver getDriver() {
        return WebDriverManager.chromedriver().create();
    }

    public static void quitDriver() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.quit();
        }
    }
    public static void closeBrowser() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.close();
        }
    }
    public static void maximizeWindow() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.manage().window().maximize();
        }
    }
    public static void deleteAllCookies() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.manage().deleteAllCookies();
        }
    }

    public static void navigateTo(String url) {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.get(url);
        }
    }

    public static void refreshPage() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.navigate().refresh();
        }
    }

    public static void goBack() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.navigate().back();
        }
    }

    public static void goForward() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.navigate().forward();
        }
    }

    public static String getCurrentUrl() {
        WebDriver driver = getDriver();
        if (driver != null) {
            return driver.getCurrentUrl();
        }
        return null;
    }

    public static String getTitle() {
        WebDriver driver = getDriver();
        if (driver != null) {
            return driver.getTitle();
        }
        return null;
    }

    public static void setImplicitWait(Duration duration) {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(duration);
        }
    }

    public static void setPageLoadTimeout(Duration duration) {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.manage().timeouts().pageLoadTimeout(duration);
        }
    }

    public static void setScriptTimeout(Duration duration) {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.manage().timeouts().scriptTimeout(duration);
        }
    }

    public static void switchToWindow(String windowHandle) {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.switchTo().window(windowHandle);
        }
    }

    public static void switchToNewWindow() {
        WebDriver driver = getDriver();
        if (driver != null) {
            // open a new tab by default; use WindowType.WINDOW for a new window
            driver.switchTo().newWindow(WindowType.TAB);
        }
    }

    public static void switchToFrame(int index) {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.switchTo().frame(index);
        }
    }

    public static void switchToFrame(String nameOrId) {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.switchTo().frame(nameOrId);
        }
    }

    public static void switchToFrame(WebElement frame) {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.switchTo().frame(frame);
        }
    }

    public static void switchToDefaultContent() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.switchTo().defaultContent();
        }
    }

    public static void switchToParentFrame() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.switchTo().parentFrame();
        }
    }

    public static Alert getAlert() {
        WebDriver driver = getDriver();
        if (driver != null) {
            return driver.switchTo().alert();
        }
        return null;
    }

    public static void closeAllBrowsers() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.quit();
        }
    }
}
