package myprojects.automation.assignment5.tests;

import bsh.Capabilities;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;
import myprojects.automation.assignment5.BaseTest;
import myprojects.automation.assignment5.model.ProductData;
import myprojects.automation.assignment5.utils.Properties;
import myprojects.automation.assignment5.utils.logging.CustomReporter;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class PlaceOrderTest extends BaseTest {
    ProductData product = null;
    int qty = -1;

    @DataProvider(name = "Authentication")
    public static Object[][] credentials() {
        return new Object[][] { { "webinar.test@gmail.com", "Xcg7299bnSmMuRLp9ITw" }};
    }

    @Test
    public void checkSiteVersion() {
        // open main page and validate website version
        actions.open();

        DesiredCapabilities cap = (DesiredCapabilities)((RemoteWebDriver)driver.getWrappedDriver()).getCapabilities();
        String browser = cap.getBrowserName().toLowerCase();
        System.out.println(browserName);
        String os = cap.getPlatform().toString();
        System.out.println(os);
        String v = cap.getVersion().toString();
        System.out.println(v);

        Assert.assertEquals(browserName, browser);
        Assert.assertEquals(isMobileTesting, isMobileTesting(browser));
    }

    /// implement order creation test
    @Test(dependsOnMethods = "checkSiteVersion", dataProvider = "Authentication")
    public void createNewOrder(String login, String password) {
        WebElement element;

        actions.open();
        // open random product
        actions.openRandomProduct();
        // save product parameters
        product = actions.getOpenedProductInfo();
        String url = driver.getCurrentUrl();


        actions.login(login, password);
        qty = actions.checkInStock(product);
        CustomReporter.log("Stock QTY - " + qty);
        Assert.assertTrue(qty != -1);
        //actions.signOut();

        driver.navigate().to(url);
        // actions.open();
        // add product to Cart and validate product information in the Cart
        actions.toElement(By.className("add-to-cart")).click();

        actions.waitForVisibilityContent(By.cssSelector(".modal-header")); // id - blockcart-modal

        Assert.assertTrue(
        actions.toElement(By.cssSelector("h4.modal-title")) //myModalLabel
                .getText()
                .indexOf("Товар добавлен в корзину") > -1);

        Assert.assertEquals(
                product.getName().toLowerCase(),
                actions.toElement(By.tagName("h6")).getText().toLowerCase());
        Assert.assertEquals(
                "В вашей карте товаров: 1.",
                actions.toElement(By.cssSelector(".modal-body .cart-products-count")).getText());

        Assert.assertTrue(
        actions.toElement(By.xpath("//*[@id=\"blockcart-modal\"]/div/div/div[2]/div/div[2]/div/p[4]"))
                .getText()
                .indexOf("Всего: " + String.valueOf(product.getPrice()).replace(".", ",")) > -1);  // Всего: 26,99
        actions.toElement(By.cssSelector("button.close")).click();
        actions.waitForInvisibleContent(By.cssSelector("button.close"));

        Assert.assertEquals("(1)", actions.toElement(By.className("cart-products-count")).getText());

        actions.toElement(By.cssSelector(".cart-preview a")).click();

        element = actions.waitForContentLoad(By.id("cart-subtotal-products"));
        actions.scrollTo(element);

        element = actions.toElement(By.cssSelector(".product-line-info a.label"));
        Assert.assertEquals(
            product.getName().toLowerCase(),
            element.getText().toLowerCase());
        element = actions.toElement(By.id("cart-subtotal-products"));
        Assert.assertEquals(
            "1 шт.",
            actions.toElement(element, By.className("label")).getText());
        Assert.assertTrue(
    actions.toElement(element, By.className("value"))
            .getText()
            .indexOf(String.valueOf(product.getPrice()).replace(".", ",")) != -1);

        // proceed to order creation, fill required information
        actions.toElement(By.className("checkout")).findElement(By.tagName("a")).click();
        actions.waitForContentLoad(By.id("checkout"));

        actions.toElement(By.name("firstname")).sendKeys("firstname");
        actions.toElement(By.name("lastname")).sendKeys("lastname");
        actions.toElement(By.name("email")).sendKeys("test@test.com");
        actions.toElement(By.name("continue")).click(); // data-link-action="register-new-customer"

        actions.toElement(By.name("address1")).sendKeys("my address");
        actions.toElement(By.name("postcode")).sendKeys("12345");
        actions.toElement(By.name("city")).sendKeys("city");
        actions.toElement(By.name("confirm-addresses")).click();

        actions.toElement(By.name("confirmDeliveryOption")).click();

        actions.toElement(By.id("payment-option-1")).click();
        actions.toElement(By.id("conditions_to_approve[terms-and-conditions]")).click();
        actions.toElement(By.cssSelector("#payment-confirmation button[type=submit]")).click();

        // place new order and validate order summary
        element = actions.waitForContentLoad(By.id("content-hook_order_confirmation"));
        Assert.assertTrue(
    element
            .findElement(By.cssSelector(".h1.card-title"))
            .getText().toLowerCase().indexOf("ваш заказ подтверждён") != -1);
        Assert.assertTrue(actions.toElement(By.className("details")).getText().toLowerCase().indexOf(product.getName().toLowerCase()) != -1);
        element = driver.findElement(By.className("qty"));
        Assert.assertEquals(
            String.valueOf(product.getQty()),
            actions.toElement(element, By.className("col-xs-2")).getText());
        Assert.assertTrue(
            actions.toElement(element, By.className("col-xs-5")).getText().indexOf(String.valueOf(product.getPrice()).replace(".", ",")) != -1);

        // check in stock updated
        driver.navigate().to(Properties.getBaseAdminUrl());
        int updatedQty = actions.checkInStock(product);
        CustomReporter.log("Updated Stock QTY - " + updatedQty);
        Assert.assertTrue(qty > updatedQty);
    }
}
