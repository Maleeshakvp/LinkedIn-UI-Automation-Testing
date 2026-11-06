package lk.ac.kdu.sqa.pages;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchBox = By.cssSelector(
        "input[placeholder*='Search'], " +
        "input[aria-label*='Search']"
    );

    private final By resultCards = By.cssSelector(
        "main a[href*='/in/'], " +
        "main a[href*='/company/'], " +
        "main a[href*='/jobs/search-results/'], " +
        "main a[href*='/feed/update/']"
    );

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
            driver, Duration.ofSeconds(30)
        );
    }

    private WebElement findVisibleSearchBox() {
        return wait.until(d -> {
            for (WebElement element : d.findElements(searchBox)) {
                try {
                    if (element.isDisplayed() && element.isEnabled()) {
                        return element;
                    }
                } catch (StaleElementReferenceException e) {
                    // Retry if the page updates.
                }
            }
            return null;
        });
    }

    // Search for the complete supplied phrase.
    public void searchFor(String term) {
        String normalizedTerm = term.trim().replaceAll("\\s+", " ");

        WebElement box = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            box = findVisibleSearchBox();
            box.click();
            box.sendKeys(Keys.CONTROL, "a");
            box.sendKeys(Keys.DELETE);

            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> {
                        for (WebElement element :
                                d.findElements(searchBox)) {
                            try {
                                if (element.isDisplayed()
                                        && element.isEnabled()
                                        && element.getAttribute("value")
                                            .isEmpty()) {
                                    return true;
                                }
                            } catch (StaleElementReferenceException e) {
                                // Retry if LinkedIn replaces the input.
                            }
                        }
                        return false;
                    });
            } catch (TimeoutException e) {
                if (attempt == 3) {
                    throw new TimeoutException(
                        "Could not clear LinkedIn's search input.",
                        e
                    );
                }
                continue;
            }

            box = findVisibleSearchBox();
            box.sendKeys(term);

            try {
                box = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> {
                        for (WebElement element :
                                d.findElements(searchBox)) {
                            try {
                                if (element.isDisplayed()
                                        && element.isEnabled()
                                        && normalizedTerm.equals(
                                            element.getAttribute("value")
                                        )) {
                                    return element;
                                }
                            } catch (StaleElementReferenceException e) {
                                // Retry if LinkedIn replaces the input.
                            }
                        }
                        return null;
                    });
                break;
            } catch (TimeoutException e) {
                if (attempt == 3) {
                    throw new TimeoutException(
                        "Search input did not retain the complete term: "
                            + normalizedTerm,
                        e
                    );
                }
            }
        }

        if (box == null) {
            throw new IllegalStateException(
                "Could not enter the search term: " + normalizedTerm
            );
        }

        System.out.println("Search term entered: " + term);

        box.sendKeys(Keys.ENTER);

        wait.until(d -> {
            String url = URLDecoder.decode(
                d.getCurrentUrl(), StandardCharsets.UTF_8
            ).toLowerCase();

            return url.contains("/search/results/")
                && url.contains(
                    "keywords=" + normalizedTerm.toLowerCase()
                );
        });

        System.out.println(
            "Actual search URL: " +
            wait.until(d -> d.getCurrentUrl())
        );
    }

    public boolean areResultsDisplayed() {
        return wait.until(d -> {
            for (WebElement result : d.findElements(resultCards)) {
                try {
                    if (result.isDisplayed()) {
                        return true;
                    }
                } catch (StaleElementReferenceException e) {
                    // Retry if the results update.
                }
            }
            return false;
        });
    }

    public String clickFirstPeopleResult() {
        By peopleLinks = By.cssSelector(
            "main a[href*='/in/']"
        );

        WebElement profileLink = wait.until(d -> {
            for (WebElement element : d.findElements(peopleLinks)) {
                try {
                    String href = element.getAttribute("href");

                    if (element.isDisplayed()
                            && element.isEnabled()
                            && href != null
                            && href.matches(
                                "https://www\\.linkedin\\.com/in/[^/?#]+/?"
                            )) {
                        return element;
                    }
                } catch (StaleElementReferenceException e) {
                    // Retry if the results page updates.
                }
            }
            return null;
        });

        String expectedUrl = profileLink.getAttribute("href");

        profileLink.click();

        wait.until(d ->
            d.getCurrentUrl().contains("linkedin.com/in/")
        );

        return expectedUrl;
    }
}