package myprojects.automation.assignment5.utils;

import myprojects.automation.assignment5.GeneralActions;
import myprojects.automation.assignment5.utils.logging.EventHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.SkipException;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DriverFactory {
    /**
     *
     * @param browser Driver type to use in tests.
     * @return New instance of {@link WebDriver} object.
     */
    public static WebDriver initDriver(String browser) {
        switch (browser) {
            case "firefox":
                System.setProperty(
                    "webdriver.gecko.driver",
                    new File(DriverFactory.class.getResource("/geckodriver.exe").getFile()).getPath());
                return new FirefoxDriver();
            case "ie":
            case "internet explorer":
                System.setProperty(
                    "webdriver.ie.driver",
                    new File(DriverFactory.class.getResource("/IEDriverServer.exe").getFile()).getPath());
                DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
                capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
                return new InternetExplorerDriver(capabilities);
            case "phantomjs":
                System.setProperty(
                    "phantomjs.binary.path",
                    new File(DriverFactory.class.getResource("/phantomjs.exe").getFile()).getPath());
                return new PhantomJSDriver();
            case "chrome":
            default:
                System.setProperty(
                    "webdriver.chrome.driver",
                    new File(DriverFactory.class.getResource("/chromedriver.exe").getFile()).getPath());
                return new ChromeDriver();
        }
    }

    /**
     *
     * @param browser Remote driver type to use in tests.
     * @param gridUrl URL to Grid.
     * @return New instance of {@link RemoteWebDriver} object.
     */
    public static WebDriver initDriver(String browser, String gridUrl) {
        WebDriver driver = null;
        // prepare capabilities for required browser and return RemoteWebDriver instance
        // create WebDriver instance according to passed parameters
        DesiredCapabilities capabilities = null;
        switch (browser) {
            case "android":
                capabilities = DesiredCapabilities.android();
                break;
            case "firefox":
                capabilities = DesiredCapabilities.firefox();
                break;
            case "ie":
            case "internet explorer":
                capabilities = DesiredCapabilities.internetExplorer();
                break;
            case "phantomjs":
                capabilities = DesiredCapabilities.phantomjs();
                break;
            case "chrome":
            default:
                capabilities = DesiredCapabilities.chrome();
                break;
        }
        try {
            driver = new RemoteWebDriver(new URL(gridUrl), capabilities);
            /*driver = new EventFiringWebDriver(new RemoteWebDriver(
                new URL("http://localhost:4444/wd/hub"),
                capabilities
            ));*/
        }catch(Exception ex) {
            ex.printStackTrace();
            throw new SkipException("Unable to create RemoteWebDriver instance!");
        }

        return driver;
    }
}
