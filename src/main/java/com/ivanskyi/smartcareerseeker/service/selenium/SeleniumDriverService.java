package com.ivanskyi.smartcareerseeker.service.selenium;

import org.openqa.selenium.WebDriver;

public interface SeleniumDriverService {

    WebDriver getDriver();

    WebDriver getDriverWithoutMediaContent();
}
