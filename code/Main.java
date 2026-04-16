package com.bank.logic;

import java.util.Scanner;

public class Main {

    private static final int VIEW_BALANCE = 1;
    private static final int DEPOSIT = 2;
    private static final int WITHDRAW = 3;
    private static final int EXIT = 4;

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            SavingsAccount account = new SavingsAccount(1000.0);
            boolean running = true;

            System.out.println("=== Dynamic Banking System ===");

            while (running) {
                displayMenu();

                int choice = readInt(scanner, "Choose an option: ");

                try {
                    switch (choice) {
                        case VIEW_BALANCE:
                            System.out.println(account.getAccountSummary());
                            break;

                        case DEPOSIT:
                            handleDeposit(scanner, account);
                            break;

                        case WITHDRAW:
                            handleWithdraw(scanner, account);
                            break;

                        case EXIT:
                            running = false;
                            System.out.println("Shutting down system...");
                            break;

                        default:
                            System.out.println("Invalid option! Please try again.");
                    }

                } catch (InsolventException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. View balance");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Exit");
    }

    private static void handleDeposit(Scanner scanner, SavingsAccount account) throws InsolventException {
        double amount = readDouble(scanner, "Enter deposit amount: ");

        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        account.processTransaction(amount);
        System.out.println("Deposit successful.");
    }

    private static void handleWithdraw(Scanner scanner, SavingsAccount account) throws InsolventException {
        double amount = readDouble(scanner, "Enter withdrawal amount: ");

        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        account.processTransaction(-amount);
        System.out.println("Withdrawal successful.");
    }

    private static int readInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);

            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // clear invalid input
            }
        }
    }

    private static double readDouble(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);

            if (scanner.hasNextDouble()) {
                return scanner.nextDouble();
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // clear invalid input
            }
        }
    }
}