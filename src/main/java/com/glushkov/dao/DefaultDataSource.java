package com.glushkov.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DefaultDataSource {

    private Properties properties = new Properties();

    public DefaultDataSource() {
        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(fileInputStream);
        } catch (IOException ioException) {
            throw new RuntimeException("Can`t read properties", ioException);
        }
    }

    public Connection getConnection() {
        try {
            String host = properties.getProperty("jdbc.host");
            String user = properties.getProperty("jdbc.user");
            String password = properties.getProperty("jdbc.password");
            return DriverManager.getConnection(host, user, password);
        }catch (SQLException sqlException){
            throw new RuntimeException("Error while connecting to db", sqlException);
        }
    }
}


