package com.btg.funds.exception;

public class DuplicateSubscriptionException extends RuntimeException {

    public DuplicateSubscriptionException(String fundName) {
        super("Ya se encuentra suscrito al fondo " + fundName);
    }
}
