
package lk.ac.kdu.sqa.tests;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import lk.ac.kdu.sqa.base.BaseTest;
import lk.ac.kdu.sqa.pages.LoginPage;
import lk.ac.kdu.sqa.pages.SearchPage;

public class SearchTests extends BaseTest {

    // Load LinkedIn credentials securely.
    private Properties loadCredentials() {
        Properties credentials = new Properties();

        try (InputStream input =
                getClass().getResourceAsStream("/credentials.properties")) {

            Assert.assertNotNull(
                input,
                "credentials.properties was not found"
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

    @Test
    public void verifySearchResultsDisplayed() {

        // Step 1: Load credentials.
        Properties credentials = loadCredentials();

        String email = credentials.getProperty("linkedin.email");
        String password = credentials.getProperty("linkedin.password");

        // Step 2: Log in to LinkedIn.
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        // Step 3: Wait for the LinkedIn feed.
        By feedSearchBox = By.cssSelector(
            "input[placeholder*='Search'], " +
            "input[aria-label*='Search']"
        );

        boolean loggedIn = new WebDriverWait(
                driver, Duration.ofMinutes(3))
            .until(d -> {

                String url = d.getCurrentUrl();

                if (url.contains("/login")
                        || url.contains("/checkpoint/")) {
                    return false;
                }

                if (!url.contains("/feed")) {
                    return false;
                }

                for (WebElement element :
                        d.findElements(feedSearchBox)) {
                    try {
                        if (element.isDisplayed()
                                && element.isEnabled()) {
                            return true;
                        }
                    } catch (StaleElementReferenceException e) {
                        // Retry if the page updates.
                    }
                }

                return false;
            });

        Assert.assertTrue(
            loggedIn,
            "The LinkedIn feed was not reached."
        );

        System.out.println("Login verified. Starting search.");

        // Step 4: Search for software testing.
        SearchPage searchPage = new SearchPage(driver);

        searchPage.searchFor("software testing");

        // Step 5: Verify search results URL.
        Assert.assertTrue(
            driver.getCurrentUrl().contains("/search/results/"),
            "The LinkedIn search results URL was not found."
        );

        // Step 6: Verify visible search results.
        Assert.assertTrue(
            searchPage.areResultsDisplayed(),
            "No visible search result was found."
        );

        // Step 7: Capture screenshot evidence.
        captureScreenshot("SearchResults_SoftwareTesting");

        System.out.println("SEARCH TEST PASSED.");
    }
}
