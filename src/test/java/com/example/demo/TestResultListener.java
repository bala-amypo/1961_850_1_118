package com.example.demo;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestResultListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        // Test started
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        // Test passed
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        // Test failed
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        // Test skipped
    }
}