/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.connection;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author to
 */
public class DBConnection {
    public static Connection getConnection() throws Exception {     
        // Le developpeur doit crée un class de connection
        // Nom du classe : connection.DBConnection
        // Avec une methode static a l'interieur qui a le signature public static Connection getConnection() throws Exception
        try {
            Class connectionClass = Class.forName("connection.DBConnection");
            
            Method connectionGetter =connectionClass.getDeclaredMethod("getConnection", new Class[0]);
            Connection connection = (Connection) connectionGetter.invoke(null, new Object[0]);
            
            return connection;
            
        } catch (ClassNotFoundException e) {
            throw new Exception("La classe connection.DBConnection, source de connection est introuvable !");
        } catch (NoSuchMethodError e) {
            throw new Exception("La fonction getConnection est introuvable !");
        }
    }
}
