/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.connection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

/**
 *
 * @author to
 */
public abstract class DBConnection {
    // static Field
    static String datasourceUrl;
    static String datasourceDatabase;
    static String datasourceUsername;
    static String datasourcePassword;
    static String datasourceType;

    // static getter and setter
    public static String getDatasourceUrl() {
        return datasourceUrl;
    }

    public static void setDatasourceUrl(String datasourceUrl) {
        DBConnection.datasourceUrl = datasourceUrl;
    }

    public static String getDatasourceDatabase() {
        return datasourceDatabase;
    }

    public static void setDatasourceDatabase(String datasourceDatabase) {
        DBConnection.datasourceDatabase = datasourceDatabase;
    }

    public static String getDatasourceUsername() {
        return datasourceUsername;
    }

    public static void setDatasourceUsername(String datasourceUsername) {
        DBConnection.datasourceUsername = datasourceUsername;
    }

    public static String getDatasourcePassword() {
        return datasourcePassword;
    }

    public static void setDatasourcePassword(String datasourcePassword) {
        DBConnection.datasourcePassword = datasourcePassword;
    }

    public static String getDatasourceType() {
        return datasourceType;
    }

    public static void setDatasourceType(String datasourceType) {
        DBConnection.datasourceType = datasourceType;
    }

    // method to override
    public abstract Connection getConnection() throws Exception;
    
    public abstract String getNextIdQuery();
    
    // Connection managing method
    // get all connection properties in the file connection.properties
    public static HashMap<String, String> getConnectionProperties() throws Exception {
        HashMap<String, String> properties = new HashMap<>();

        FileReader reader;
        try {
            reader = new FileReader("connection.properties");
        } catch (FileNotFoundException e) {
            throw new Exception("Le fichier de propriété connection.properties est introuvable !");
        }

        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] division = line.split("=");
                properties.put(division[0], division[1]);
            }
        }

        return properties;
    }
    
    // set the connection properties to the DBConnection field
    public static void setConnectionProperties() throws Exception {
        HashMap<String, String> properties = getConnectionProperties();

        String url = properties.get("datasource.url");
        if (url == null) {
            throw new Exception("La propriété datasource.url doit être spécifié dans le fichier connection.properties");
        } else {
            setDatasourceUrl(url);
        }
        
        String database = properties.get("datasource.database");
        if (database == null) {
            throw new Exception("La propriété datasource.database doit être spécifié dans le fichier connection.properties");
        } else {
            setDatasourceDatabase(database);
        }

        String username = properties.get("datasource.username");
        if (username == null) {
            throw new Exception("La propriété datasource.username doit être spécifié dans le fichier connection.properties");
        } else {
            setDatasourceUsername(username);
        }

        String password = properties.get("datasource.password");
        if (password == null) {
            throw new Exception("La propriété datasource.password doit être spécifié dans le fichier connection.properties");
        } else {
            setDatasourcePassword(password);
        }
        
        String type = properties.get("datasource.type");
        if (type == null) {
            throw new Exception("La propriété datasource.type doit être spécifié dans le fichier connection.properties");
        } else {
            setDatasourceType(type);
        }
    }
    
    // get the convenable connection manager according to the datasource type
    public static DBConnection getConnectionManager() throws Exception {
        setConnectionProperties();
        
        switch (getDatasourceType()) {
            case "mysql":
                return new MysqlConnection();
            case "postgres":
                return new PostgresConnection();
            default:
                throw new Exception("Ce type de base de données n'est pas encore pris en charge !");
        }
    }

    
    public static void main(String[] args) throws IOException, Exception {
        DBConnection.getConnectionManager().getConnection();
    }
}
