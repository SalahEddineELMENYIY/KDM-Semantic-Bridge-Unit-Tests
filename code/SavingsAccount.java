package com.bank.logic;

import java.util.ArrayList;
import java.util.List;

public class SavingsAccount implements IBankService {

    private static final double DEFAULT_INTEREST_RATE = 0.02;

    private double balance;
    private double interestRate;
    private final List<String> history = new ArrayList<>();

    public SavingsAccount(double initialBalance) {
        this(initialBalance, DEFAULT_INTEREST_RATE);
    }

    public SavingsAccount(double initialBalance, double interestRate) {
        if (initialBalance < 0 || interestRate < 0) {
            throw new IllegalArgumentException("Invalid initial values");
        }
        this.balance = initialBalance;
        this.interestRate = interestRate;
        history.add("Account opened with: " + initialBalance);
    }

    @Override
    public void processTransaction(double amount) {
        if (amount < 0 && Math.abs(amount) > balance) {
            throw new InsolventException("Insufficient balance: " + amount);
        }
        balance += amount;
    }

    public synchronized void applyInterest() {
        double interest = balance * interestRate;
        balance += interest;
        history.add(String.format("Interest applied: %.2f", interest));
    }

    @Override
    public String getAccountSummary() {
        return String.format("Balance: %.2f | Transactions: %d", balance, history.size());
    }

    public double getBalance() {
        return balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public List<String> getHistory() {
        return List.copyOf(history);
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "balance=" + balance +
                ", interestRate=" + interestRate +
                '}';
    }
}