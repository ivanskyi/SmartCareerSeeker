package com.ivanskyi.smartcareerseeker.util;

import com.ivanskyi.smartcareerseeker.service.selenium.SeleniumDriverService;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SeleniumDriverServiceImpl implements SeleniumDriverService {

    private final String chromeDriverPath;

    public SeleniumDriverServiceImpl(@Value("${chrome.driver.path}") String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
    }

    @Override
    public WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    @Override
    public WebDriver getDriverWithoutMediaContent() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        ChromeOptions options = createHeadlessChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    private ChromeOptions createHeadlessChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--blink-settings=imagesEnabled=false");
        return options;
    }
}
