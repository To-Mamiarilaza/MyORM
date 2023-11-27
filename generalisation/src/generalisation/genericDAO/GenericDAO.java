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
    public static void executeUpdate(String sql, Connection connection) throws Exception {
        // Execution d'une update
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            System.out.println("QUERY : " + sql);
            throw e;
        } finally {
            statement.close();
        }
    }

    public static void executePreparedStatement(String sql, Object object, Connection connection) throws Exception {
        // Execution d'une update
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            GenericQuery.setAllStatementValue(statement, object);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("QUERY : " + statement.toString());
            throw e;
        } finally {
            statement.close();
        }
    }

    public static <T extends Object> List executeQuery(String sql, T object, Connection connection) throws Exception {
        // Execution d'une selection
        Statement statement = null;
        ResultSet resultat = null;
        try {
            statement = connection.createStatement();
            resultat = statement.executeQuery(sql);

            List<T> listes = new ArrayList();
            while (resultat.next()) {
                // Crée un objet vide pour recevoir les lignes du table
                Object temp = GenericUtil.newEmptyObject(object.getClass());
                GenericDAO.setFieldsvalue(resultat, temp, connection);
                listes.add((T) temp);
            }
            return listes;
        } catch (Exception e) {
            System.out.println("QUERY : " + sql);
            throw e;
        } finally {
            statement.close();
            if (resultat != null) {
                resultat.close();
            }
        }
    }
    
    

/// Fonctions principale
    // change completement la ligne d'un table en cette object
    public static void updateById(Object object, Object id, Connection connection) throws Exception {
        String sql = GenericQuery.getUpdateQuery(object, id);
        try {
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
            }

            GenericDAO.executePreparedStatement(sql, object, connection);

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

    public static void updateById(Object object, Connection connection) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(object);
        Object id = GenericUtil.getFieldValue(object, pk.getName());
        
        updateById(object, id, connection);
    }
    
    public static void updateBydId(Object object) throws Exception {
        updateById(object, null);
    }
    
    

    public static void deleteById(Class objectClass, Object id, Connection connection) throws Exception {
        String errorDisplay = null;
        try {
            Object object = GenericUtil.newEmptyObject(objectClass);

            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
            }

            String sql = GenericQuery.getDeleteQuery(object, id);
            errorDisplay = sql;
            GenericDAO.executeUpdate(sql, connection);

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
                connection = DBConnection.getConnection();
            }

            GenericUtil.giveObjectId(object, connection);
            GenericDAO.executePreparedStatement(sql, object, connection);

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

    public static void save(Object object) throws Exception {
        save(object, null);
    }

    // selection d'un object dans un table par son ID
    public static <T extends Object> T findById(Class objectClass, Object id, Connection connection) throws Exception {
        try {
            Object object = GenericUtil.newEmptyObject(objectClass);

            String sql = GenericQuery.getByIdQuery(object, id);
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
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
            e.printStackTrace();
            connection.close();
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
                connection = DBConnection.getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, object, connection);
            if (closeIndice == 0) {
                connection.close();
            }

            return listes;
        } catch (Exception e) {
            e.printStackTrace();
            connection.close();
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
                connection = DBConnection.getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, object, connection);
            if (closeIndice == 0) {
                connection.close();
            }

            return listes;
        } catch (Exception e) {
            e.printStackTrace();
            connection.close();
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
                connection = DBConnection.getConnection();
            }

            GenericDAO.executeUpdate(sql, connection);

            if (closeIndice == 0) {
                connection.commit();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
            connection.close();
            throw e;
        }
    }

    public static void directUpdate(String sql) throws Exception {
        directUpdate(sql, null);
    }
}
