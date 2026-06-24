package com.sireesha.userservice.exception;

public class UserAlreadyExistException extends BusinessException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
