package com.ivanskyi.smartcareerseeker.service.impl;

import com.ivanskyi.smartcareerseeker.constants.DjinniConstants;
import com.ivanskyi.smartcareerseeker.model.Vacancy;
import com.ivanskyi.smartcareerseeker.service.DjinniService;
import com.ivanskyi.smartcareerseeker.service.selenium.SeleniumDriverService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DjinniServiceImpl implements DjinniService {

    private static final int SHORT_DELAY_IN_SECONDS = 3;
    public static final int FIVE_SECONDS = 5;
    private final Logger logger = LoggerFactory.getLogger(DjinniServiceImpl.class);

    private final SeleniumDriverService seleniumDriverService;

    private final String login;
    private final String password;

    private static final List<Vacancy> ALL_PARSED_VACANCIES = new ArrayList<>();

    @Autowired
    public DjinniServiceImpl(
            final SeleniumDriverService seleniumDriverService,
            @Value("${djinni.login}") String login,
            @Value("${djinni.password}") String password
    ) {
        this.seleniumDriverService = seleniumDriverService;
        this.login = login;
        this.password = password;
    }

    @Override
    public void startApplyingProcess() {
        logger.info("Start applying process for djinni.co");
        final WebDriver driver = seleniumDriverService.getDriver();
        signIn(driver);
        waitSeconds(SHORT_DELAY_IN_SECONDS);
        checkAllMySubscriptions(driver);
        logger.info("Finished applying process for djinni.co");
    }

    private void signIn(final WebDriver driver) {
        logger.info("Started sign in djinni.co");
        driver.get(DjinniConstants.Endpoint.LOGIN_PAGE.getValue());
        driver.findElement(By.xpath(DjinniConstants.XPath.SIGN_IN_EMAIL_FIELD.getValue())).sendKeys(login);
        driver.findElement(By.xpath(DjinniConstants.XPath.SIGN_IN_PASSWORD_FIELD.getValue())).sendKeys(password);
        driver.findElement(By.xpath(DjinniConstants.XPath.SIGN_IN_SIGN_IN_BUTTON.getValue())).click();
        logger.info("Finished sign in djinni.co");
    }

    private void checkAllMySubscriptions(final WebDriver driver) {
        logger.info("Commenced verification of subscriptions");
        driver.get(DjinniConstants.Endpoint.MY_SUBS.getValue());
        for (String subscriptionLink : getSubscriptionLinks(driver)) {
            visitSubscriptionPage(driver, subscriptionLink);
        }
        logger.info("Finished to check my subscriptions");
    }

    private void visitSubscriptionPage(final WebDriver driver, final String subscriptionLink) {
        final int numberOfPages = getPageCount(driver);
        for (int i = 1; i <= numberOfPages; i++) {
            getPositionsFromPage(driver, modifyURL(subscriptionLink, i));
            waitSeconds(FIVE_SECONDS);
        }
    }

    public static String modifyURL(final String originalURL, final int pageNumber) {
        final String pageParam = "page=" + pageNumber;
        boolean hasQueryParams = originalURL.contains("?");
        if (originalURL.contains("&page=")) {
            return originalURL.replaceAll("&page=\\d+", "&" + pageParam);
        } else if (originalURL.endsWith("/")) {
            return originalURL + "?" + pageParam;
        } else if (hasQueryParams) {
            return originalURL + "&" + pageParam;
        } else {
            return originalURL + "?" + pageParam;
        }
    }

    private void getPositionsFromPage(final WebDriver driver, final String subscriptionLink) {
        driver.get(subscriptionLink);
        final List<WebElement> vacancies = driver.findElements(By.className(DjinniConstants.XPath.MY_SUBSCRIPTION_JOB_TITLE.getValue()));
        for (WebElement vacancy : vacancies) {
            ALL_PARSED_VACANCIES.add(new Vacancy(vacancy.getText(), vacancy.getText(), vacancy.getText()));
        }
    }

    private List<String> getSubscriptionLinks(final WebDriver driver) {
        logger.info("Started to extract all subscriptions links.");
        final WebElement subBlock = driver.findElement(By.className("jobs-subscription-list"));
        final List<WebElement> linkElements = subBlock.findElements(By.tagName("a"));
        return linkElements.stream()
                .map(linkElement -> linkElement.getAttribute("href"))
                .collect(Collectors.toList());
    }

    private int getPageCount(final WebDriver driver) {
        final List<WebElement> paginationButtons = driver.findElements(By.cssSelector(".pagination_with_numbers li"));
        int countOfPages = 0;
        for (WebElement webElement : paginationButtons) {
            if (isButtonWithNumberOfPage(webElement)) {
                countOfPages++;
            }
        }
        return countOfPages;
    }

    private boolean isButtonWithNumberOfPage(final WebElement button) {
        final String textFromButton = button.getText().replaceAll("\\D", "").trim();
        try {
            int numberOfPage = Integer.parseInt(textFromButton);
            return numberOfPage > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void waitSeconds(final int countOfSeconds) {
        try {
            Thread.sleep(countOfSeconds * 1000L);
        } catch (Exception e) {
            logger.error("Got error when tried to make delay.");
        }
    }
}
