package com.ivanskyi.smartcareerseeker.service.impl;

import com.ivanskyi.smartcareerseeker.constants.DjinniConstants;
import com.ivanskyi.smartcareerseeker.constants.Symbols;
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
        waitSeconds(Symbols.THREE);
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
        getSubscriptionLinks(driver).forEach(subscriptionLink -> visitSubscriptionPage(driver, subscriptionLink));
        logger.info("Finished to check my subscriptions");
    }

    private void visitSubscriptionPage(final WebDriver driver, final String subscriptionLink) {
        final int numberOfPages = getPageCount(driver);
        for (int i = Symbols.ONE; i <= numberOfPages; i++) {
            getPositionsFromPage(driver, modifyURL(subscriptionLink, i));
            waitSeconds(Symbols.FIVE);
        }
        System.out.println(ALL_PARSED_VACANCIES);
    }

    public static String modifyURL(final String originalURL, final int pageNumber) {
        final String pageParam = DjinniConstants.Other.PAGE_ATTRIBUTE.getValue() + pageNumber;
        boolean hasQueryParams = originalURL.contains(Symbols.QUESTION_MARK);
        if (originalURL.contains(Symbols.AND + DjinniConstants.Other.PAGE_ATTRIBUTE.getValue())) {
            return originalURL.replaceAll(DjinniConstants.Other.PAGE_ATTRIBUTE_WITH_REGEX.getValue(), Symbols.AND + pageParam);
        } else if (originalURL.endsWith(Symbols.SLASH)) {
            return originalURL + Symbols.QUESTION_MARK + pageParam;
        } else if (hasQueryParams) {
            return originalURL + Symbols.AND + pageParam;
        } else {
            return originalURL + Symbols.QUESTION_MARK + pageParam;
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
        final WebElement subBlock = driver.findElement(By.className(DjinniConstants.Other.JOBS_SUBSCRIPTION_LIST_CLASS_NAME.getValue()));
        final List<WebElement> linkElements = subBlock.findElements(By.tagName(DjinniConstants.Other.A_TAG_NAME.getValue()));
        return linkElements.stream()
                .map(linkElement -> linkElement.getAttribute(DjinniConstants.Other.HREF_ATTRIBUTE_NAME.getValue()))
                .collect(Collectors.toList());
    }

    private int getPageCount(final WebDriver driver) {
        final List<WebElement> paginationButtons = driver.findElements(By.cssSelector(DjinniConstants.Other.PAGINATION_BUTTONS_CSS_SELECTOR.getValue()));
        int countOfPages = Symbols.ZERO;
        for (WebElement webElement : paginationButtons) {
            if (isButtonWithNumberOfPage(webElement)) {
                countOfPages++;
            }
        }
        return countOfPages;
    }

    private boolean isButtonWithNumberOfPage(final WebElement button) {
        final String textFromButton = button.getText().replaceAll(Symbols.REGEX_NON_DIGIT, Symbols.EMPTY_SPACE).trim();
        try {
            int numberOfPage = Integer.parseInt(textFromButton);
            return numberOfPage > Symbols.ZERO;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void waitSeconds(final int countOfSeconds) {
        try {
            Thread.sleep(countOfSeconds * Symbols.MILLISECONDS_IN_ONE_SECOND);
        } catch (InterruptedException e) {
            logger.error("Got error when tried to make delay.");
            Thread.currentThread().interrupt();
        }
    }
}
