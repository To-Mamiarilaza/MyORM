/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connection;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author To Mamiarilaza
 */
public class DBConnection {
    public static Connection getConnection() throws Exception {     
        // Fonction qui renvoie la connection vers la base : 
            String database = "generalisation";       // Nom de la base
            String user = "postgres";       // User dans postgres
            String mdp = "postgres";       // Mot de passe
            
            // Creation de l'objet de connection
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database, user,  mdp);
            
            connection.setAutoCommit(false);
            return connection;
    }
}
