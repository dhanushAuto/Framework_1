package factories;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class BrowserFactory {

    private BrowserFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static WebDriver createBrowser(String browser) {
        if (System.getProperty("cloud") != null && System.getProperty("cloud").equalsIgnoreCase("true")) {
            return createRemoteBrowser(browser);
        }

        switch (browser.toLowerCase()) {
            case "chrome":
                return new ChromeDriver();
            case "firefox":
                return new FirefoxDriver();
            case "edge":
                return new EdgeDriver();
            default:
                throw new IllegalArgumentException("Invalid Browser : " + browser);
        }
    }

    private static WebDriver createRemoteBrowser(String browser) {
        String username = System.getProperty("cloud.username");
        String accessKey = System.getProperty("cloud.key");
        String hubUrl = "https://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub";

        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", browser);
        
        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "10");
        capabilities.setCapability("bstack:options", bstackOptions);

        try {
            return new RemoteWebDriver(new URL(hubUrl), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Cloud Hub URL", e);
        }
    }
}
