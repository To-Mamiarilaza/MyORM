/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.connection;

import generalisation.annotations.DBTable;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author to
 */
public class PostgresConnection extends DBConnection {
    
    // To get the next id query
    public String getNextIdQuery() {
        return "SELECT nextval('%s')";
    }
    
    // Fonction qui renvoie la connection vers la base : 
    public Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");

        // Creation de l'objet de connection
        Connection connection = DriverManager.getConnection(PostgresConnection.getDatasourceUrl() + PostgresConnection.getDatasourceDatabase(), PostgresConnection.getDatasourceUsername(), PostgresConnection.getDatasourcePassword());

        connection.setAutoCommit(false);

        return connection;
    }

}
