package com.danalyzer.common;


import org.apache.commons.validator.UrlValidator;

/**
 * Created by Light on 10/01/16.
 */
public class WebUtils {

    private static UrlValidator urlValidator = new UrlValidator();

    public static boolean isValidUrl(String url) {
        return urlValidator.isValid(url);
    }
}
