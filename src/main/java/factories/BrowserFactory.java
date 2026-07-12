package factories;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

    public class BrowserFactory {
        
        private BrowserFactory() {
            throw new IllegalStateException("Utility class");
        }

        public static WebDriver createBrowser(String browser) {

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
    }
