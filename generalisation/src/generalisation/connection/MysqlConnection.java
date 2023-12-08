/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.connection;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author to
 */
public class MysqlConnection extends DBConnection {

    // To get the next id query
    @Override
    public String getNextIdQuery() {
        return "";
    }
    
    // Fonction qui renvoie la connection vers la base : 
    @Override
    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Creation de l'objet de connection
        Connection connection = DriverManager.getConnection(MysqlConnection.getDatasourceUrl() + MysqlConnection.getDatasourceDatabase(), MysqlConnection.getDatasourceUsername(), MysqlConnection.getDatasourcePassword());

        connection.setAutoCommit(false);

        return connection;
    }
}
