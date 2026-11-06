package lk.ac.kdu.sqa.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By emailField =
            By.cssSelector("input[type='email']");

    private final By passwordField =
            By.cssSelector("input[type='password']");

    private final By signInButton = By.xpath(
    	    "//button[@type='submit']" +
    	    " | //button[normalize-space(.)='Sign in']" +
    	    " | //input[@type='submit']"
    	);
    

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(20));
    }

    public void open() {
        driver.get("https://www.linkedin.com/login");
    }

    private WebElement findVisibleElement(By locator) {
        return wait.until(d -> {
            for (WebElement element : d.findElements(locator)) {
                try {
                    if (element.isDisplayed() && element.isEnabled()) {
                        return element;
                    }
                } catch (StaleElementReferenceException e) {
                    // Retry if the page changes.
                }
            }
            return null;
        });
    }

    public void enterEmail(String email) {
        WebElement field = findVisibleElement(emailField);
        field.clear();
        field.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement field = findVisibleElement(passwordField);
        field.clear();
        field.sendKeys(password);
    }

    public void clickSignIn() {
        WebElement button = wait.until(d -> {
            for (WebElement candidate : d.findElements(signInButton)) {
                try {
                    if (candidate.isDisplayed() && candidate.isEnabled()) {
                        return candidate;
                    }
                } catch (StaleElementReferenceException e) {
                    // Retry if the page updates.
                }
            }
            return null;
        });

        System.out.println("Selenium is clicking Sign in.");
        button.click();
    }
    
    public boolean isLoginFormDisplayed() {
        return findVisibleElement(emailField).isDisplayed();
    }

    public boolean isPasswordFieldDisplayed() {
        return findVisibleElement(passwordField).isDisplayed();
    }
}