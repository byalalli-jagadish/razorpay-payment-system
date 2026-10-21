package com.razorpay.common.exception;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String currentStatus, String event) {
        super("Invalid payment state transition from " + currentStatus + " using event " + event);
    }
}
