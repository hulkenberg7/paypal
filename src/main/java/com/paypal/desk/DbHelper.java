package com.paypal.desk;

//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private static final Connection connection = getConnection();

    private static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/paypal?useLegacyDatetimeCode=false&serverTimezone=UTC",
                    "root",
                    "root"
            );

            System.out.println("Connection successful");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static int createUser(String firstName, String lastName) {

        try {
            PreparedStatement prepStatement = connection.prepareStatement(
                    "insert into users (first_name, last_name) " +
                            "values(?,?);"
            );

            prepStatement.setString(1, firstName);
            prepStatement.setString(2, lastName);
            prepStatement.executeUpdate();

            String idSql = "select max(id) from users";
            Statement idStatement = connection.createStatement();
            ResultSet resultSet = idStatement.executeQuery(idSql);

            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Updates the user balance in database
     * Sets balance = balance + amount
     *
     * @param userId id of the user in users table
     * @param amount double value of the amount to insert
     */

    //TODO add synchronization, exclude concurrency
    static boolean cashFlow(int userId, double amount) {
        if(amount == 0) {
            System.out.println("Reduntant operation");
            return false;
        }

        try {
            PreparedStatement prepStatement = connection.prepareStatement(
                    "update users " +
                            " set balance = balance + ? " +
                            " where id = ?"
            );
            prepStatement.setDouble(1, amount);
            prepStatement.setInt(2, userId);
            prepStatement.execute();

            if(amount > 0) {
                System.out.println("Cash in successful");
            } else {
                System.out.println("Cash out successful");
            }
            return true;
        } catch(SQLException e) {
            String sqlState = e.getSQLState();

            switch(sqlState) {
                case "45000" :
                    System.out.println("User " + userId + " has insufficient funds to complete transaction.");
                    break;
                default :
                    System.out.println("Cash out failed with message: " +  e.getMessage());
            }
            return false;
        }
    }

    /**
     * Emulates a transaction between 2 users
     * Takes money from one account and adds to another account
     *
     * @param userFrom source user id
     * @param userTo   target user id
     * @param amount   transaction amount
     */
    static void transaction(int userFrom, int userTo, double amount) {

        if(!cashFlow(userFrom, -amount) || !cashFlow(userTo, amount)) {
            System.out.println("Transaction failed");
            return;
        }

        try {
            PreparedStatement prepStatement = connection.prepareStatement(
                    "insert into transactions (user_from, user_to, transaction_amount) " +
                            "values(?,?,?);");

            prepStatement.setInt(1, userFrom);
            prepStatement.setInt(2, userTo);
            prepStatement.setDouble(3, amount);
            prepStatement.execute();
            System.out.println("Transaction successful");
        } catch(SQLException e) {
            System.out.println("Transaction failed with message " + e.getMessage());
        }

    }

    static List<User> listUsers() {
        String sql = "select * from users";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            List<User> userList = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                double balance = resultSet.getDouble("balance");

                userList.add(new User(
                        id, firstName, lastName, balance
                ));
            }
            return userList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
