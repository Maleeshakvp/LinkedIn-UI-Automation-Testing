# LinkedIn UI Automation Testing

## Project Overview

This project implements automated UI testing for LinkedIn using Java, Selenium WebDriver, TestNG, and Apache Maven. It follows the Page Object Model (POM) design pattern to separate page interactions from test logic.

The automated tests cover successful login, invalid-password validation, searching for content, and opening a profile from search results.

## Technologies Used

- Java 17 or later
- Selenium WebDriver 4.25.0
- TestNG 7.10.2
- Apache Maven
- Google Chrome and ChromeDriver
- Visual Studio Code
- Git and GitHub

## Project Structure

- `src/test/java/lk/ac/kdu/sqa/base` — Browser setup, teardown, and screenshot capture
- `src/test/java/lk/ac/kdu/sqa/pages` — Page Object Model classes
- `src/test/java/lk/ac/kdu/sqa/tests` — Automated test classes
- `src/test/java/lk/ac/kdu/sqa/utils` — Reusable utilities
- `src/test/resources/credentials.properties` — Private login credentials (excluded from Git)
- `target/screenshots` — Automatically generated screenshots
- `target/surefire-reports` — TestNG execution reports
- `evidence` — Selected screenshots for assignment documentation
- `pom.xml` — Maven configuration
- `testng.xml` — TestNG suite configuration

## Credential Configuration

Create a file named `credentials.properties` inside:

`src/test/resources/`

Add the following placeholder values:

```properties
linkedin.email=YOUR_EMAIL_ADDRESS
linkedin.password=YOUR_PASSWORD
```

Replace the placeholders with your own LinkedIn credentials.

**Important:** Never upload real login credentials to GitHub. The `.gitignore` file excludes `credentials.properties` and generated Maven files.

## How to Run the Project

1. Install Java 17 or later, Apache Maven, and Google Chrome.
2. Open Visual Studio Code and install the Extension Pack for Java.
3. Open the project folder containing `pom.xml`.
4. Open the integrated terminal.
5. Verify the installed tools:

```powershell
java -version
mvn -version
```

6. Configure `credentials.properties` with your own LinkedIn account credentials.
7. Run the complete automated test suite:

```powershell
mvn test
```

8. Review the test results in the terminal and the `target/surefire-reports` folder.

To run only the login tests:

```powershell
mvn test -Dtest=LoginTests
```

## Implemented Test Cases

| Test ID | Test Case | Expected Result |
|---|---|---|
| TC01 | Successful login | LinkedIn authenticated home feed is displayed |
| TC02 | Invalid-password login | Appropriate error message is displayed |
| TC03 | Search functionality | Search results are displayed for "software testing" |
| TC04 | Open a search result | Selected LinkedIn profile opens successfully |
| TC05 | TestNG framework check | TestNG executes the framework verification test |

## Test Execution Results

The completed full test execution reported:

- Total tests: 5
- Passed: 5
- Failed: 0
- Errors: 0
- Skipped: 0

**Overall Result: PASS**

The TestNG report confirmed that all five test methods executed successfully.

## Test Evidence

The following evidence screenshots are maintained in the `evidence` folder:

- `TC01_SuccessfulLogin.png`
- `TC02_InvalidPassword_Error.png`
- `TC03_SearchResults.png`
- `TC04_OpenLinkedInProfile.png`
- `TC05_TestNG_ExecutionReport.png`

The screenshots demonstrate the tested functionality and execution results. Personal account information should be redacted before submission.

## Automation Design

The project follows the Page Object Model (POM) architecture.

- `BaseTest` manages browser initialization, cleanup, and screenshots.
- `LoginPage` handles LinkedIn login page interactions.
- `SearchPage` handles searching and selecting results.
- Test classes use TestNG assertions to verify expected behavior.
- Selenium explicit waits handle asynchronous loading and dynamic elements.

## Challenges and Limitations

LinkedIn is a third-party application with a frequently changing interface. Dynamic page elements, authentication checkpoints, and CAPTCHA challenges may affect automated test execution.

The tests use explicit waits and appropriate locators to improve reliability. Security checkpoints must not be bypassed. If verification is required, testing should be paused or handled manually in accordance with LinkedIn's rules.

UI automation against a live external website cannot guarantee consistent results because the application, network conditions, and security mechanisms are outside the tester's control.

## Security and Ethical Considerations

- Use only an account you own and are authorized to access.
- Do not automate CAPTCHA solving or bypass authentication checkpoints.
- Never commit `credentials.properties` or passwords.
- Review screenshots and recordings for personal information.
- Use the automation only for the educational testing activities described in the assignment.

## Academic Context

Module: IT3153 — Software Quality Assurance

Assignment: 04 — LinkedIn UI Automation Testing

General Sir John Kotelawala Defence University, Faculty of Computing.
