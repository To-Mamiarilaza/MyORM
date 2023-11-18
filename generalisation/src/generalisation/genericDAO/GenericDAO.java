/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.GenericDAO;

import generalisation.annotations.DBField;
import java.sql.*;
import generalisation.connection.DBConnection;
import generalisation.utils.GenericQuery;
import generalisation.utils.GenericUtil;
import java.lang.reflect.Constructor;
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
    public static Object[] getResultArguments(ResultSet resultat, Object objet, Connection connection) throws Exception {
        Field[] champs = GenericUtil.getDBField(objet);
        Object[] arguments = new Object[champs.length];
        for (int i = 0; i < champs.length; i++) {
            if (champs[i].getType() == int.class) {
                arguments[i] = resultat.getInt(champs[i].getAnnotation(DBField.class).name());
            } else if (champs[i].getType() == double.class) {
                arguments[i] = resultat.getDouble(champs[i].getAnnotation(DBField.class).name());
            } else if (champs[i].getType() == float.class) {
                arguments[i] = resultat.getFloat(champs[i].getAnnotation(DBField.class).name());
            } else if (champs[i].getType() == boolean.class) {
                arguments[i] = resultat.getBoolean(champs[i].getAnnotation(DBField.class).name());
            } else if (champs[i].getType() == String.class) {
                arguments[i] = resultat.getString(champs[i].getAnnotation(DBField.class).name());
            } else if (champs[i].getType() == Integer.class) {
                String stringValue = resultat.getString(champs[i].getAnnotation(DBField.class).name());
                if (stringValue == null) {
                    arguments[i] = null;
                } else {
                    arguments[i] = resultat.getInt(champs[i].getAnnotation(DBField.class).name());
                }
            } else if (champs[i].getType() == Double.class) {
                arguments[i] = resultat.getDouble(champs[i].getAnnotation(DBField.class).name());
            } else if (champs[i].getType() == Float.class) {
                arguments[i] = resultat.getFloat(champs[i].getAnnotation(DBField.class).name());
            } else if (champs[i].getType() == Boolean.class) {
                arguments[i] = resultat.getBoolean(champs[i].getAnnotation(DBField.class).name());
            } else if (champs[i].getType() == LocalDate.class) {
                Date date = resultat.getDate(champs[i].getAnnotation(DBField.class).name());
                arguments[i] = date == null ? null : date.toLocalDate();
            } else if (champs[i].getType() == LocalDateTime.class) {
                Timestamp dateTime = resultat.getTimestamp(champs[i].getAnnotation(DBField.class).name());
                arguments[i] = dateTime == null ? null : dateTime.toLocalDateTime();
            } else if (champs[i].getType() == LocalTime.class) {
                Time time = resultat.getTime(champs[i].getAnnotation(DBField.class).name());
                arguments[i] = time == null ? null : time.toLocalTime();
            } else if (champs[i].getAnnotation(DBField.class).isForeignKey()) {
                // On prends l'objet referencer si c'est un foreign key
                if (champs[i].getAnnotation(DBField.class).getChild()) {
                    arguments[i] = findById(champs[i].getType(), resultat.getString(champs[i].getAnnotation(DBField.class).name()), connection);
                } else {
                    Object emptyObject = GenericUtil.newEmptyObject(champs[i].getType());
                    GenericUtil.setObjectIdManually(emptyObject, resultat.getObject(champs[i].getAnnotation(DBField.class).name()));
                    arguments[i] = emptyObject;
                }
            }
        }
        return arguments;
    }

    public static void executeUpdate(String sql, Connection connection) throws Exception {
        // Execution d'une update
        Statement statement = null;
        try {
            statement = connection.createStatement();
            System.out.println(sql);
            statement.executeUpdate(sql);
        } catch (Exception e) {
            throw e;
        } finally {
            statement.close();
        }
    }
    
    public static void executePreparedStatement(String sql, Object objet, Connection connection) throws Exception {
        // Execution d'une update
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            GenericQuery.setAllStatementValue(statement, objet);
            
            System.out.println(statement.toString());
            statement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            statement.close();
        }
    }

    public static <T extends Object> List executeQuery(String sql, T objet, Connection connection) throws Exception {
        // Execution d'une selection
        Statement statement = null;
        ResultSet resultat = null;
        try {
            statement = connection.createStatement();
            System.out.println(sql);
            resultat = statement.executeQuery(sql);

            List<T> listes = new ArrayList();
            while (resultat.next()) {
                Constructor construct = GenericUtil.getClassConstructor(objet);
                Object[] arguments = GenericDAO.getResultArguments(resultat, objet, connection);
                listes.add((T) construct.newInstance(arguments));
            }
            return listes;
        } catch (Exception e) {
            throw e;
        } finally {
            statement.close();
            if (resultat != null) {
                resultat.close();
            }
        }
    }

/// Fonctions principale
    // change completement la ligne d'un table en cette objet
    public static void updateById(Object objet, Object id, Connection connection) throws Exception {
        try {
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
            }

            String sql = GenericQuery.getUpdateQuery(objet, id);
            GenericDAO.executePreparedStatement(sql,objet, connection);

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

    public static void deleteById(Class objectClass, Object id, Connection connection) throws Exception {
        try {
            Object objet = GenericUtil.newEmptyObject(objectClass);

            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
            }

            String sql = GenericQuery.getDeleteQuery(objet, id);
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

    // Enregistre un objet dans sa table correspondante
    public static void save(Object objet, Connection connection) throws Exception {
        try {
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
            }

            GenericUtil.giveObjectId(objet, connection);
            String sql = GenericQuery.getSaveQuery(objet);
            GenericDAO.executePreparedStatement(sql, objet, connection);

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

    // selection d'un objet dans un table par son ID
    public static <T extends Object> T findById(Class objectClass, Object id, Connection connection) throws Exception {
        try {
            Object objet = GenericUtil.newEmptyObject(objectClass);

            String sql = GenericQuery.getByIdQuery(objet, id);
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, objet, connection);
            if (listes.size() == 0) {
                return null;
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

    // selection de tous les objets dans un table
    public static <T extends Object> List getAll(Class objectClass, String suppl, Connection connection) throws Exception {
        try {
            Object objet = GenericUtil.newEmptyObject(objectClass);

            String sql = GenericQuery.getAllQuery(objet, suppl);
            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, objet, connection);
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

    // Selection de tous les objets dans un table par requete directe et on donne l'objet dans la quelle on recoit le resultat
    public static <T extends Object> List directQuery(Class objectClass, String sql, Connection connection) throws Exception {
        try {
            Object objet = GenericUtil.newEmptyObject(objectClass);

            int closeIndice = 1;    // 0 si on doit le fermer et 1 sinon
            if (connection == null) {
                closeIndice = 0;
                connection = DBConnection.getConnection();
            }

            List<T> listes = GenericDAO.executeQuery(sql, objet, connection);
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

    // Permettant de faire un update directe ie sans avoir besoin d'un objet mais changement directe
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
}
