package com.ivanskyi.smartcareerseeker.constants;

public class DjinniConstants {
    public enum XPath {
        SIGN_IN_EMAIL_FIELD("/html/body/div[1]/div[2]/div/div/div[2]/form/div[1]/div[1]/input"),
        SIGN_IN_PASSWORD_FIELD("/html/body/div[1]/div[2]/div/div/div[2]/form/div[1]/div[2]/input"),
        SIGN_IN_SIGN_IN_BUTTON("/html/body/div[1]/div[2]/div/div/div[2]/form/div[1]/div[3]/button"),
        MY_SUBSCRIPTION_JOB_TITLE("list-jobs__title"),
        MY_SUBSCRIPTION_JOB_DESCRIPTION_BLOCK("profile"),
        MY_SUBSCRIPTION_JOB_DESCRIPTION_BLOCK_HREF("href");

        private final String value;

        XPath(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Endpoint {
        LOGIN_PAGE("https://djinni.co/login"),
        MY_SUBS("https://djinni.co/jobs/my-subs/?exp_years=1y&primary_keyword=Java&exp_level=2y&page=1"),
        MY_FAVOURITE("https://djinni.co/jobs/my-favorites/");

        private final String value;

        Endpoint(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    public enum Other {
        PAGE_ATTRIBUTE("page="),
        PAGE_ATTRIBUTE_WITH_REGEX("&page=\\d+"),
        JOBS_SUBSCRIPTION_LIST_CLASS_NAME("jobs-subscription-list"),
        A_TAG_NAME("a"),
        HREF_ATTRIBUTE_NAME("href"),
        PAGINATION_BUTTONS_CSS_SELECTOR(".pagination_with_numbers li");

        private final String value;

        Other(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
