package com.testtribe.api.utils;

import java.util.regex.Pattern;

/** The only place where secrets are masked before they reach a logger. */
public final class LogMasker {

    private static final String MASK = "****";

    private static final Pattern JSON_SECRET =
            Pattern.compile("(\"(?:password|token)\"\\s*:\\s*\")[^\"]*(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern COOKIE_TOKEN =
            Pattern.compile("(token=)[^;\\s,\"]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern AUTHORIZATION =
            Pattern.compile("(Authorization\\s*[=:]\\s*)[^\\r\\n]+", Pattern.CASE_INSENSITIVE);

    private LogMasker() {
    }

    public static String mask(String text) {
        if (text == null) {
            return null;
        }
        String masked = JSON_SECRET.matcher(text).replaceAll("$1" + MASK + "$2");
        masked = COOKIE_TOKEN.matcher(masked).replaceAll("$1" + MASK);
        return AUTHORIZATION.matcher(masked).replaceAll("$1" + MASK);
    }
}
