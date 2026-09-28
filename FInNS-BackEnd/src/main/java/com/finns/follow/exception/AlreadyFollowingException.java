package com.finns.follow.exception;

public class AlreadyFollowingException extends RuntimeException {
    public AlreadyFollowingException(String message) {
        super(message);
    }
}