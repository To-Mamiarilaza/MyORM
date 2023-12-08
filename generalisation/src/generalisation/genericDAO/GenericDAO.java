/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.genericDAO;

import generalisation.annotations.DBField;
import generalisation.annotations.DBTable;
import java.sql.*;
import generalisation.connection.DBConnection;
import generalisation.utils.GenericQuery;
import generalisation.utils.GenericUtil;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

/**
 *
 * @author to
 */
public class GenericDAO {

    // Field
    static DBConnection connectionManager;
    public enum Action {
        INSERT, UPDATE, DELETE, SELECT
    }
    
    // Getter and setter
    public static DBConnection getConnectionManager() {
        return connectionManager;
    }

    public static void setConnectionManager(DBConnection connectionManager) {
        GenericDAO.connectionManager = connectionManager;
    }

    // Get convenable connection
    public static Connection getConnection() throws Exception {
        if (getConnectionManager() == null) {
            setConnectionManager(DBConnection.getConnectionManager());
        }

        return getConnectionManager().getConnection();
    }

    // Les types pris en charge sont : int , double, float, boolean, Integer, Double, Float, Boolean, String, LocalDate, LocalDateTime, LocalTime
    public static void setFieldsvalue(ResultSet resultat, Object object, Connection connection) throws Exception {
        Field[] champs = GenericUtil.getDBField(object);
        for (int i = 0; i < champs.length; i++) {
            // On prends l'object referencer si c'est un foreign key
            if (champs[i].getAnnotation(DBField.class).isForeignKey()) {
                if (resultat.getString(champs[i].getAnnotation(DBField.class).name()) == null) {
                    GenericUtil.setFieldValue(object, champs[i], null);
                } else if (champs[i].getAnnotation(DBField.class).getChild()) {
                    GenericUtil.setFieldValue(object, champs[i], findById(champs[i].getType(), resultat.getString(champs[i].getAnnotation(DBField.class).name()), connection));
                } else {
                    Object emptyObject = GenericUtil.newEmptyObject(champs[i].getType());
                    GenericUtil.setObjectIdManually(emptyObject, resultat.getObject(champs[i].getAnnotation(DBField.class).name()));
                    GenericUtil.setFieldValue(object, champs[i], emptyObject);
                }
            } else {
                try {
                    GenericUtil.setFieldValue(object, champs[i], resultat.getObject(champs[i].getAnnotation(DBField.class).name(), champs[i].getType()));
                } catch (Exception e) {
                    System.out.println("Un problème survient au niveau de la colonne " + champs[i].getAnnotation(DBField.class).name());
                    throw e;
                }
            }
        }
    }

    /// Les fonctions d'éxécution
    // Execute an update
    public static void executeUpdate(String sql, Object object, Connection connection, Action action) throws Exception {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (sql.contains("?")) {
                GenericQuery.setStatementValue(statement, object, true, action);
            }
            
            statement.executeUpdate();
            
            // autoincrement <==> integer
            if (action == Action.INSERT && object.getClass().getAnnotation(DBTable.class).autoIncrement()) {
                Field pk = GenericUtil.getPrimaryKeyField(object);
                resultSet = statement.getGeneratedKeys();
                resultSet.next(); 
                Integer newId = resultSet.getInt(pk.getAnnotation(DBField.class).name());
                GenericUtil.setFieldValue(object, pk, newId);
            }
            
        } catch (Exception e) {
            System.out.println("QUERY : " + statement.toString());
            throw e;
        } finally {
            statement.close();
        }
    }

    // execute a query
    public static <T extends Object> List executeQuery(String sql, Object object, Connection connection) throws Exception {

        PreparedStatement statement = null;
        ResultSet resultat = null;

        try {
            statement = connection.prepareStatement(sql);
            if (sql.contains("?")) {
                GenericQuery.setStatementValue(statement, object, false, Action.SELECT);
            }
            resultat = statement.executeQuery();

            List<T> listes = new ArrayList();
            while (resultat.next()) {
                // Crée un objet vide pour recevoir les lignes du table
                Object temp = GenericUtil.newEmptyObject(object.getClass());
                GenericDAO.setFieldsvalue(resultat, temp, connection);
                listes.add((T) temp);
            }

            return listes;
        } catch (Exception e) {
            System.out.println("QUERY : " + statement.toString());
            throw e;
        } finally {
            if (resultat != null) {
                resultat.close();
            }
            statement.close();
        }
    }

/// Fonctions principale
    // Count the number of row in a table
    public static int count(Class objectClass, Connection connection) throws Exception {
        Object object = GenericUtil.newEmptyObject(objectClass);
        
        Statement statement = null;
        ResultSet resultset = null;
        
        try {
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            String sql = GenericQuery.getCountQuery(object);
            statement = connection.createStatement();
            resultset = statement.executeQuery(sql);

            resultset.next();
            int count = resultset.getInt("count");
            
            if (closeIndice == 0) {
                connection.close();
            }
            
            return count;
        } catch (Exception e) {
            if (resultset != null) resultset.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            
            throw e;
        }
    }
    
    public static int count(Class objectClass) throws Exception {
        return count(objectClass, null);
    }

    // change completement la ligne d'un table en cette object
    public static void update(Object object, Object id, Connection connection) throws Exception {
        String sql = GenericQuery.getUpdateQuery(object, id);
        try {
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            GenericDAO.executeUpdate(sql, object, connection, Action.UPDATE);

            if (closeIndice == 0) {
                connection.commit();
                connection.close();
            }
        } catch (Exception e) {
            connection.rollback();
            connection.close();
            throw e;
        }
    }

    public static void update(Object object, Connection connection) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(object);
        Object id = GenericUtil.getFieldValue(object, pk.getName());

        update(object, id, connection);
    }

    public static void update(Object object) throws Exception {
        update(object, null);
    }

    public static void deleteById(Class objectClass, Object id, Connection connection) throws Exception {
        String errorDisplay = null;
        try {
            Object object = GenericUtil.newEmptyObject(objectClass);

            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            String sql = GenericQuery.getDeleteQuery(object, id);
            errorDisplay = sql;
            GenericDAO.executeUpdate(sql, null, connection, Action.DELETE);

            if (closeIndice == 0) {
                connection.commit();
                connection.close();
            }
        } catch (Exception e) {
            connection.rollback();
            connection.close();
            throw e;
        }
    }

    public static void deleteById(Class objectClass, Object id) throws Exception {
        deleteById(objectClass, id, null);
    }

    public static void deleteById(Object object, Connection connection) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(object);
        Object id = GenericUtil.getFieldValue(object, pk.getName());

        deleteById(object.getClass(), id, connection);
    }

    public static void deleteById(Object object) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(object);
        Object id = GenericUtil.getFieldValue(object, pk.getName());

        deleteById(object.getClass(), id, null);
    }

    // Enregistre un object dans sa table correspondante
    public static void save(Object object, Connection connection) throws Exception {
        String sql = GenericQuery.getSaveQuery(object);
        try {
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            // Si l' ID dépend d'un séquence
            if (GenericUtil.hasPrimaryKeyField(object)) {
                if (!object.getClass().getAnnotation(DBTable.class).sequenceName().equals("")) {
                    GenericUtil.giveObjectId(object, connection);
                } else if (object.getClass().getAnnotation(DBTable.class).autoIncrement()) {
                    sql = GenericUtil.setPrimaryKeyToDefault(sql);
                }
            }

            // Execution du mis a jour et prendre l' ID inséré
            GenericDAO.executeUpdate(sql, object, connection, Action.INSERT);

            if (closeIndice == 0) {
                connection.commit();
                connection.close();
            }
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
                connection.close();
            }
            throw e;
        }
    }

    public static void save(Object object) throws Exception {
        save(object, null);
    }

    // Where multicritere
    public static <T extends Object> List find(Object object, Connection connection) throws Exception {
        try {
            String sql = GenericQuery.getFindQuery(object);

            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, object, connection);

            if (closeIndice == 0) {
                connection.close();
            }

            return listes;
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            throw e;
        }
    }

    public static <T extends Object> List find(Object object) throws Exception {
        return find(object, null);
    }

    // selection d'un object dans un table par son ID
    public static <T extends Object> T findById(Class objectClass, Object id, Connection connection) throws Exception {
        try {
            Object object = GenericUtil.newEmptyObject(objectClass);

            String sql = GenericQuery.getByIdQuery(object, id);
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, object, connection);
            if (listes.size() == 0) {
                throw new Exception("Aucun " + object.getClass().getAnnotation(DBTable.class).name() + " portant cette ID existe !");
            }
            T resultat = (T) listes.get(0);

            if (closeIndice == 0) {
                connection.close();
            }

            return resultat;
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            throw e;
        }
    }

    public static <T extends Object> T findById(Class objectClass, Object id) throws Exception {
        return findById(objectClass, id, null);
    }

    // selection de tous les objects dans un table
    public static <T extends Object> List getAll(Class objectClass, String suppl, Connection connection) throws Exception {
        try {
            Object object = GenericUtil.newEmptyObject(objectClass);

            String sql = GenericQuery.getAllQuery(object, suppl);
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, object, connection);
            if (closeIndice == 0) {
                connection.close();
            }

            return listes;
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            throw e;
        }
    }

    public static <T extends Object> List getAll(Class objectClass, Connection connection) throws Exception {
        return getAll(objectClass, null, connection);
    }

    public static <T extends Object> List getAll(Class objectClass) throws Exception {
        return getAll(objectClass, null, null);
    }

    // Selection de tous les objects dans un table par requete directe et on donne l'object dans la quelle on recoit le resultat
    public static <T extends Object> List directQuery(Class objectClass, String sql, Connection connection) throws Exception {
        try {
            Object object = GenericUtil.newEmptyObject(objectClass);

            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, object, connection);
            if (closeIndice == 0) {
                connection.close();
            }

            return listes;
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            throw e;
        }
    }

    public static <T extends Object> List directQuery(Class objectClass, String sql) throws Exception {
        return directQuery(objectClass, sql, null);
    }

    // Permettant de faire un update directe ie sans avoir besoin d'un object mais changement directe
    public static void directUpdate(String sql, Connection connection) throws Exception {
        try {
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = getConnection();
            }

            GenericDAO.executeUpdate(sql, null, connection, Action.UPDATE);

            if (closeIndice == 0) {
                connection.commit();
                connection.close();
            }
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
                connection.close();
            }
            throw e;
        }
    }

    public static void directUpdate(String sql) throws Exception {
        directUpdate(sql, null);
    }
}
