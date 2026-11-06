
package lk.ac.kdu.sqa.base;

import java.nio.file.Files;
import java.nio.file.Path;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    // Capture a screenshot at any point during a test.
    protected void captureScreenshot(String screenshotName) {
        if (driver == null) {
            return;
        }

        try {
            Path screenshotDirectory =
                    Path.of("target", "screenshots");

            Files.createDirectories(screenshotDirectory);

            // Keep filenames safe and readable.
            String safeName = screenshotName.replaceAll(
                    "[^a-zA-Z0-9_-]", "_"
            );

            String fileName =
                    safeName + "_"
                    + System.currentTimeMillis()
                    + ".png";

            Path screenshotPath =
                    screenshotDirectory.resolve(fileName);

            byte[] screenshot =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.BYTES);

            Files.write(screenshotPath, screenshot);

            System.out.println(
                    "Screenshot saved: "
                    + screenshotPath.toAbsolutePath()
            );

        } catch (Exception e) {
            System.out.println(
                    "Could not save screenshot: "
                    + e.getMessage()
            );
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        if (driver != null) {
            try {
                String status;

                if (result.getStatus() == ITestResult.SUCCESS) {
                    status = "PASS";
                } else if (result.getStatus() == ITestResult.FAILURE) {
                    status = "FAIL";
                } else {
                    status = "SKIP";
                }

                captureScreenshot(
                        result.getMethod().getMethodName()
                        + "_" + status
                );

            } finally {
                driver.quit();
                driver = null;
            }
        }
    }
}
