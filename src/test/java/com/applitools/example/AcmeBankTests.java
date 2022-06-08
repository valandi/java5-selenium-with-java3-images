package com.applitools.example;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class AcmeBankTests {
    private static String applitoolsApiKey;
    private static boolean headless;

    private static BatchInfo batch;
    private static Configuration config;
    private static VisualGridRunner runner;

    private WebDriver driver;
    private Eyes eyes;
    private com.applitools.eyes.images.Eyes imageEyes;

    @BeforeAll
    public static void setUpConfigAndRunner() {
        applitoolsApiKey = System.getenv("APPLITOOLS_API_KEY");

        headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "true"));

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
        batch = new BatchInfo("eyes-images-java3 and eyes-selenium-java5 incompatibility test");

        config = new Configuration();
        config.setApiKey(applitoolsApiKey);
        config.setBatch(batch);
        config.addBrowser(800, 600, BrowserType.CHROME);
        config.addBrowser(1600, 1200, BrowserType.FIREFOX);
        config.addBrowser(1024, 768, BrowserType.SAFARI);
        config.addDeviceEmulation(DeviceName.Pixel_2, ScreenOrientation.PORTRAIT);
        config.addDeviceEmulation(DeviceName.Nexus_10, ScreenOrientation.LANDSCAPE);
    }

    @BeforeEach
    public void openBrowserAndEyes(TestInfo testInfo) {
        driver = new ChromeDriver(new ChromeOptions().setHeadless(headless));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        eyes = new Eyes(runner);
        eyes.setConfiguration(config);

        imageEyes = new com.applitools.eyes.images.Eyes();
        imageEyes.setConfiguration(config);

        eyes.open(
                driver,
                "ACME Bank Web App",
                testInfo.getDisplayName(),
                new RectangleSize(1024, 768)
        );

        imageEyes.open(
                "ACME Bank WEb App",
                "ACME image test"
        );
    }

    @Test
    public void logIntoBankAccount() {
        driver.get("https://demo.applitools.com");

        imageEyes.checkImage("src/test/java/com/applitools/example/iphone.png");
        eyes.check(Target.window().fully().withName("Login page"));

        driver.findElement(By.id("username")).sendKeys("applibot");
        driver.findElement(By.id("password")).sendKeys("I<3VisualTests");
        driver.findElement(By.id("log-in")).click();

        eyes.check(Target.window().fully().withName("Main page").layout());
    }

    @AfterEach
    public void cleanUpTest() {
        driver.quit();

        eyes.closeAsync();
        imageEyes.closeAsync();
    }

    @AfterAll
    public static void printResults() {
        TestResultsSummary allTestResults = runner.getAllTestResults();
        System.out.println(allTestResults);
    }
}