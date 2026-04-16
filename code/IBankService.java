package com.bank.logic;

public interface IBankService {
    void processTransaction(double amount) throws InsolventException;
    String getAccountSummary();
}