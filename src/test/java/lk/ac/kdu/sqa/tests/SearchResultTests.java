
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

public class SearchResultTests extends BaseTest {

    // Load credentials from the private properties file.
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

        return credentials;
    }

    @Test
    public void verifyFirstPeopleResultOpens() {

        // Step 1: Load credentials.
        Properties credentials = loadCredentials();

        String email = credentials.getProperty("linkedin.email");
        String password = credentials.getProperty("linkedin.password");

        Assert.assertNotNull(email, "LinkedIn email is missing.");
        Assert.assertNotNull(password, "LinkedIn password is missing.");

        // Step 2: Log in.
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        // Step 3: Wait for the authenticated feed.
        By feedSearchBox = By.cssSelector(
            "input[placeholder*='Search'], " +
            "input[aria-label*='Search'], " +
            "input[role='combobox']"
        );

        new WebDriverWait(driver, Duration.ofMinutes(3))
            .until(d -> {

                if (!d.getCurrentUrl().contains("/feed")) {
                    return false;
                }

                for (WebElement element :
                        d.findElements(feedSearchBox)) {
                    try {
                        if (element.isDisplayed()) {
                            return true;
                        }
                    } catch (StaleElementReferenceException e) {
                        // Retry if the page updates.
                    }
                }

                return false;
            });

        // Step 4: Search for software testing.
        SearchPage searchPage = new SearchPage(driver);

        searchPage.searchFor("software testing");

        Assert.assertTrue(
            searchPage.areResultsDisplayed(),
            "Search results should be visible."
        );

        // Step 5: Open the first visible people result.
        String expectedUrl = searchPage.clickFirstPeopleResult();

        // Step 6: Verify the opened profile.
        Assert.assertTrue(
            expectedUrl.contains("linkedin.com/in/"),
            "The selected result should be a LinkedIn profile."
        );

        Assert.assertTrue(
            driver.getCurrentUrl().contains("linkedin.com/in/"),
            "The selected LinkedIn profile page did not open."
        );

        // Step 7: Capture the opened profile as evidence.
        captureScreenshot("OpenedLinkedInProfile");

        System.out.println("SEARCH RESULT TEST PASSED.");
    }
}
