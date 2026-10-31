package com.testtribe.api.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        log.info("START {}.{}", result.getTestClass().getName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("PASS  {} ({} ms)", result.getMethod().getMethodName(), duration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable t = result.getThrowable();
        log.error("FAIL  " + result.getMethod().getMethodName() + " - "
                + (t == null ? "no message" : LogMasker.mask(t.getMessage())), t);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("SKIP  {}", result.getMethod().getMethodName());
    }

    private static long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
