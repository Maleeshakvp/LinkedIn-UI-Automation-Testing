
package lk.ac.kdu.sqa.tests;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import lk.ac.kdu.sqa.base.BaseTest;
import lk.ac.kdu.sqa.pages.LoginPage;

public class LoginTests extends BaseTest {

    // Load LinkedIn credentials from the private properties file.
    private Properties loadCredentials() {

        Properties credentials = new Properties();

        try (InputStream input =
                getClass().getResourceAsStream(
                    "/credentials.properties")) {

            Assert.assertNotNull(
                input,
                "credentials.properties was not found."
            );

            credentials.load(input);

        } catch (Exception e) {
            throw new AssertionError(
                "Could not load LinkedIn credentials.", e
            );
        }

        Assert.assertNotNull(
            credentials.getProperty("linkedin.email"),
            "LinkedIn email is missing."
        );

        Assert.assertNotNull(
            credentials.getProperty("linkedin.password"),
            "LinkedIn password is missing."
        );

        return credentials;
    }

    // Identify known LinkedIn invalid-password messages.
    private boolean containsInvalidPasswordMessage(String text) {

        if (text == null) {
            return false;
        }

        String normalized = text.toLowerCase()
            .replaceAll("\\s+", " ");

        return normalized.contains("wrong email or password")
            || normalized.contains("incorrect email or password")
            || normalized.contains("incorrect username or password")
            || normalized.contains(
                "password you entered is incorrect")
            || normalized.contains("your password was incorrect")
            || normalized.contains(
                "email or password is incorrect");
    }

    // Find the smallest visible element containing the error.
    private WebElement findVisibleErrorElement() {

        List<WebElement> elements = driver.findElements(
            By.cssSelector("body *")
        );

        WebElement bestMatch = null;
        int shortestTextLength = Integer.MAX_VALUE;

        for (WebElement element : elements) {

            try {
                if (!element.isDisplayed()) {
                    continue;
                }

                String text = element.getText();

                if (!containsInvalidPasswordMessage(text)) {
                    continue;
                }

                // Prefer the element with the shortest matching text.
                // This avoids scrolling to the entire page body.
                if (text.length() < shortestTextLength) {
                    shortestTextLength = text.length();
                    bestMatch = element;
                }

            } catch (StaleElementReferenceException e) {
                // Ignore elements changed by LinkedIn.
            }
        }

        return bestMatch;
    }

    // POSITIVE TEST: Login using valid credentials.
    @Test(priority = 1)
    public void verifySuccessfulLogin() {

        Properties credentials = loadCredentials();

        String email =
            credentials.getProperty("linkedin.email");

        String password =
            credentials.getProperty("linkedin.password");

        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        System.out.println("Sign-in submitted.");

        By searchBox = By.cssSelector(
            "input[placeholder*='Search'], " +
            "input[aria-label*='Search'], " +
            "input[role='combobox']"
        );

        try {

            boolean loggedIn = new WebDriverWait(
                    driver, Duration.ofMinutes(3))
                .until(d -> {

                    String url = d.getCurrentUrl();

                    if (url.contains("/login")
                            || url.contains("/checkpoint/")) {
                        return false;
                    }

                    for (WebElement element :
                            d.findElements(searchBox)) {

                        try {
                            if (element.isDisplayed()) {
                                return true;
                            }

                        } catch (StaleElementReferenceException e) {
                            // Retry after page update.
                        }
                    }

                    return false;
                });

            Assert.assertTrue(
                loggedIn,
                "The LinkedIn feed/search box was not found."
            );

            captureScreenshot(
                "SuccessfulLogin_FeedDisplayed"
            );

            System.out.println(
                "POSITIVE LOGIN TEST PASSED."
            );

        } catch (TimeoutException e) {

            Assert.fail(
                "Login could not be verified. Current URL: "
                    + driver.getCurrentUrl(),
                e
            );
        }
    }

    // NEGATIVE TEST: Login using an incorrect password.
    @Test(priority = 2)
    public void verifyInvalidPasswordShowsError() {

        Properties credentials = loadCredentials();

        String email =
            credentials.getProperty("linkedin.email");

        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.enterEmail(email);

        // Intentionally incorrect password.
        loginPage.enterPassword(
            "WrongPasswordForTestOnly_9182!"
        );

        loginPage.clickSignIn();

        System.out.println(
            "Negative login submitted."
        );

        try {

            String result = new WebDriverWait(
                    driver, Duration.ofSeconds(20))
                .until(d -> {

                    if (d.getCurrentUrl()
                            .contains("/checkpoint/")) {
                        return "CHECKPOINT";
                    }

                    try {

                        String pageText = d.findElement(
                            By.tagName("body")
                        ).getText();

                        if (containsInvalidPasswordMessage(
                                pageText)) {
                            return "ERROR";
                        }

                    } catch (StaleElementReferenceException e) {
                        return null;
                    }

                    return null;
                });

            if ("CHECKPOINT".equals(result)) {

                throw new SkipException(
                    "LinkedIn security verification " +
                    "prevented the invalid-password assertion."
                );
            }

            Assert.assertEquals(
                result,
                "ERROR",
                "Expected an incorrect-password message."
            );

            System.out.println(
                "Incorrect-password message verified."
            );

            // Locate the actual error message.
            WebElement errorElement =
                findVisibleErrorElement();

            Assert.assertNotNull(
                errorElement,
                "The error was detected, but its visible " +
                "HTML element could not be located."
            );

            // Scroll the error message into the center.
            JavascriptExecutor js =
                (JavascriptExecutor) driver;

            js.executeScript(
                "arguments[0].scrollIntoView(" +
                "{block:'center', inline:'nearest'});",
                errorElement
            );

            // Wait for any scrolling animation to finish.
            try {
                Thread.sleep(700);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Confirm the error is inside the screenshot area.
            Boolean errorInViewport =
                (Boolean) js.executeScript(
                    "var r = arguments[0].getBoundingClientRect();" +
                    "return r.top < window.innerHeight && " +
                    "r.bottom > 0;",
                    errorElement
                );

            Assert.assertTrue(
                Boolean.TRUE.equals(errorInViewport),
                "The error message is not inside " +
                "the screenshot viewport."
            );

            // Capture the actual error message.
            captureScreenshot(
                "InvalidPassword_ErrorMessage"
            );

            System.out.println(
                "NEGATIVE LOGIN TEST PASSED."
            );

        } catch (TimeoutException e) {

            Assert.fail(
                "Incorrect-password message was not detected. " +
                "Current URL: " + driver.getCurrentUrl(),
                e
            );
        }
    }
}
