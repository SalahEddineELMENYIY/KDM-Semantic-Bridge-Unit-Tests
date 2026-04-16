package com.bank.logic;

public class InsolventException extends RuntimeException{
    public InsolventException(String message) {
        super(message);
    }
}