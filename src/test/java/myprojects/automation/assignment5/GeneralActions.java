package myprojects.automation.assignment5;


import myprojects.automation.assignment5.model.ProductData;
import myprojects.automation.assignment5.utils.Properties;
import myprojects.automation.assignment5.utils.logging.CustomReporter;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;

/**
 * Contains main script actions that may be used in scripts.
 */
public class GeneralActions {
    private WebDriver driver;
    private WebDriverWait wait;

    public GeneralActions(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }

    /**
     * Logs in to Admin Panel.
     * @param login
     * @param password
     */
    public void login(String login, String password) {
        CustomReporter.log("Login as user - " + login);
        driver.navigate().to(Properties.getBaseAdminUrl());

        driver
            .findElement(By.id("email"))
            .sendKeys(login);

        driver
            .findElement(By.id("passwd"))
            .sendKeys(password);

        driver
            .findElement(By.name("submitLogin"))
            .click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("main")));
    }

    public void signOut() {
        toElement(By.className("employee-dropdown")).click();

        waitForVisibilityContent(By.id("header_logout")).click();

        // check the action was success
        waitForContentLoad(By.name("submitLogin"));
    }

    public void openRandomProduct() {
        // implement logic to open random product before purchase
        WebElement element;

        element = driver.findElement(By.cssSelector(".all-product-link"));
        scrollTo(element);
        element.click();

        waitForContentLoad(By.tagName("main"));
        List<WebElement> products = driver.findElements(By.tagName("article"));
        Random random = (new Random());
        int index = random.nextInt(products.size()) + 1;
        // index = 0; <--- HOTFIX: 2017/4/24 - site was updated then by someone reason product details was incorrect

        System.out.println("Producst index: " + index);
        element = products.get(index).findElement(By.tagName("a"));
        element.click();

        waitForContentLoad(By.id("product"));
    }

    /**
     * Extracts product information from opened product details page.
     *
     * @return
     */
    public ProductData getOpenedProductInfo() {
        CustomReporter.logAction("Get information about currently opened product");
        // extract data from opened page
        // #product-details, json - > name, quantity, price
       ProductData product = new ProductData(
            driver.findElement(By.cssSelector("h1[itemprop='name']")).getText(),
            1 /*Integer.valueOf(driver.findElement(By.cssSelector(".product-quantities span")).getText())*/,
            Float.valueOf(driver.findElement(By.cssSelector(".current-price span[itemprop='price']")).getAttribute("content").replace(',', '.'))
       );
       return product;
    }

    /**
     * check in stock
     */
    public int checkInStock(ProductData product) {
        WebElement element;

        driver.findElement(By.id("subtab-AdminCatalog")).click();

        element = driver.findElement(By.name("products_filter_reset"));
        if(element.isDisplayed()) { element.click(); }

        element = waitForContentLoad(By.name("filter_column_name"));
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), product.getName());

        driver.findElement(By.name("filter_column_reference")).sendKeys("");
        element = waitForClickable(By.name("products_filter_submit"));
        element.click();

        element = waitForContentLoad(By.xpath("//*[@id=\"product_catalog_list\"]/div[2]/div/table/tbody/tr/td[3]"));
        CustomReporter.log("check the name - " + element.getAttribute("value"));
        Assert.assertEquals(element.getText().toLowerCase(), product.getName().toLowerCase());
        element = toElement(By.className("product-sav-quantity"));
        CustomReporter.log("check the qty - " + element.getText());
        return Integer.valueOf(element.getText());
    }

    /**
     * Open the shop
     */
    public void open() {
        driver.navigate().to(Properties.getBaseUrl());
        waitForContentLoad(By.id("main"));
    }

    public WebElement toElement(By locator) {
        WebElement element = driver.findElement(locator);
        scrollTo(element);
        return element;
    }

    public WebElement toElement(WebElement container, By locator) {
        WebElement element = container.findElement(locator);
        scrollTo(element);
        return element;
    }

    /**
     * Wait until page loader disappears from the page
     */
    public WebElement waitForContentLoad(By locator) {
        // return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        // implement generic method to wait until page content is loaded
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait until element is clickable
     */
    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForVisibilityContent(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForInvisibleContent(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Scroll Visibility Scope of the Page To Element
     * @param element
     */
    public void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

}
