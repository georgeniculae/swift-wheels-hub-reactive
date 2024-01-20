package com.swiftwheelshub.lib.exceptionhandling;

public class SwiftWheelsHubException extends RuntimeException {

    public SwiftWheelsHubException(String message) {
        super(message);
    }

    public SwiftWheelsHubException(Throwable e) {
        super(e);
    }

}
