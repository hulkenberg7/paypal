package com.paypal.desk;

import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;

public class PaypalDesk {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Welcome to paypal");
        System.out.println("Enter command");

        while (true) {
            System.out.println("(C) -> Create user");
            System.out.println("(L) -> List users");
            System.out.println("(+) -> Cash in");
            System.out.println("(-) -> Cash out");
            System.out.println("(T) -> Transaction");
            System.out.println("(Q) -> Quit");

            String command = scanner.nextLine();

            switch (command) {
                case "C":
                    createUser();
                    break;
                case "L":
                    listUsers();
                    break;
                case "+":
                    cashIn();
                    break;
                case "-":
                    cashOut();
                    break;
                case "T":
                    transaction();
                    break;
                case "Q":
                    return;
            }
        }
    }

    private static void createUser() {
        System.out.print("First name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine();

        int userId = DbHelper.createUser(
                firstName, lastName
        );

        if (userId != -1) {
            System.out.println(
                    MessageFormat.format(
                            "User {0} created successfully",
                            userId
                    )
            );
        } else {
            System.out.println(
                    "Error while creating the user"
            );
        }
    }

    private static void listUsers() {
        List<User> users = DbHelper.listUsers();
        if (users == null) return;

        for (User user : users) {
            System.out.println(user);
        }
    }

    private static void cashIn() {
        int userId = getUserIdFromConsole("User id: ");
        double amount = getAmountFromConsole();

        DbHelper.cashFlow(userId, amount);
    }

    private static void cashOut() {
        int userId = getUserIdFromConsole("User id: ");
        double amount = getAmountFromConsole();

        DbHelper.cashFlow(userId, -amount);
    }

    private static void transaction() {
        int userFrom = getUserIdFromConsole(
                "User id: "
        );
        int userTo = getUserIdFromConsole(
                "Target user id: "
        );

        double amount = getAmountFromConsole();

        DbHelper.transaction(
                userFrom, userTo, amount
        );


    }


    private static int getUserIdFromConsole(String message) {
        System.out.print(message);
        return Integer.parseInt(
                scanner.nextLine()
        );
    }

    private static double getAmountFromConsole() {
        System.out.println("Amount: ");
        return Double.parseDouble(
                scanner.nextLine()
        );
    }
}
