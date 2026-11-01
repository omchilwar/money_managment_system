package com.moneymanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/finance_db";

    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static Connection getConnection() {

        try {
            Connection con = DriverManager.getConnection(
                    URL, USER, PASSWORD
            );

            System.out.println("Database Connected Successfully!");

            return con;

        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}