/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.utils;

import generalisation.annotations.DBField;
import generalisation.annotations.DBTable;
import generalisation.genericDAO.GenericDAO.Action;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;

/**
 *
 * @author to
 */
public class GenericQuery {
    /// Ce classe est resposable de tous les constructions de requete

    // Set the ? value, if all true, set all else set only not null field
    public static void setStatementValue(PreparedStatement statement, Object object, boolean all, Action action) throws Exception {
        Field[] fields = GenericUtil.getDBField(object);
        Object[] valeurs = GenericUtil.parseObjectValue(object);

        int indice = 1;     // The ? indice
        
        // Décaler le DEFAULT si il y a un primary key
        int startIndice = 0;
        
        if (action == Action.INSERT && object.getClass().getAnnotation(DBTable.class).autoIncrement() && GenericUtil.hasPrimaryKeyField(object)) {
            startIndice = 1;
        }
        
        for (int i = startIndice; i < valeurs.length; i++) {
            if (fields[i].getAnnotation(DBField.class).isForeignKey()) {
                if (valeurs[i] != null) {
                    Field pk = GenericUtil.getPrimaryKeyField(valeurs[i]);
                    Object foreignId = GenericUtil.getFieldValue(valeurs[i], pk.getName());
                    statement.setObject(indice, foreignId);
                    indice++;
                } else if (valeurs[i] == null && all) {
                    statement.setObject(indice, null);
                    indice++;
                }
            } else {
                try {
                    if (all || valeurs[i] != null) {
                        statement.setObject(indice, valeurs[i]);
                        indice++;
                    }
                } catch (Exception e) {
                    System.out.println("Un problème survient au niveau de la colonne : " + fields[i].getType());
                    throw e;
                }
            }
        }
    }

    public static String toDBFormat(Object object) throws Exception {
        String resultat = null;

        if (object == null) {
            resultat = "null";
        } else if (object != null && object.getClass().isAnnotationPresent(DBTable.class)) {
            Field pk = GenericUtil.getPrimaryKeyField(object);
            resultat = "'" + GenericUtil.getFieldValue(object, pk.getName()).toString() + "'";
        } else {
            resultat = "'" + object.toString() + "'";
        }

        return resultat;
    }
    
    // Query construction method
    
    public static String getCountQuery(Object object) {
        return "SELECT COUNT(*) as count FROM " + object.getClass().getAnnotation(DBTable.class).name();
    }

    public static String getUpdateClause(Object object) throws Exception {
        Field[] champs = GenericUtil.getDBField(object);

        String sql = "";
        for (int i = 0; i < champs.length; i++) {
            sql += champs[i].getAnnotation(DBField.class).name() + " = ?, ";
        }
        return sql.substring(0, sql.length() - 2);
    }

    public static String getWhereClause(Object object) throws Exception {
        Field[] fields = GenericUtil.getDBField(object);
        Object[] valeurs = GenericUtil.parseObjectValue(object);

        String whereClause = "";
        for (int i = 0; i < fields.length; i++) {
            if (valeurs[i] != null) {
                whereClause += fields[i].getAnnotation(DBField.class).name() + " = ? AND ";
            }
        }
        whereClause = whereClause.substring(0, whereClause.length() - " AND ".length());       // Drop the and at the end

        return whereClause;
    }

    public static String getInsertStatement(Object object) throws Exception {
        Field[] fields = GenericUtil.getDBField(object);
        String insertValue = "";
        for (int i = 0; i < fields.length; i++) {
            insertValue += "?, ";
        }
        insertValue = insertValue.substring(0, insertValue.length() - 2);
        return insertValue;
    }

    /// Operation main query method
    
    public static String getSaveQuery(Object object) throws Exception {
        if (object.getClass().getAnnotation(DBTable.class) == null) {
            throw new Exception("Vous devez annoter la classe " + object.getClass().getName() + " avec DBTable pour pouvoir effectuer les opérations !");
        }
        
        String insertStatement = getInsertStatement(object);       // Prends tous les valeurs des champs de l'object
        String sql = "INSERT INTO " + object.getClass().getAnnotation(DBTable.class).name() + " VALUES (" + insertStatement + ")";
        return sql;
    }

    public static String getUpdateQuery(Object object, Object id) throws Exception {
        if (object.getClass().getAnnotation(DBTable.class) == null) {
            throw new Exception("Vous devez annoter la classe " + object.getClass().getName() + " avec DBTable pour pouvoir effectuer les opérations !");
        }
        
        Field pk = GenericUtil.getPrimaryKeyField(object);
        String updateClause = getUpdateClause(object);
        String sql = "UPDATE " + object.getClass().getAnnotation(DBTable.class).name() + " SET " + updateClause + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String getDeleteQuery(Object object, Object id) throws Exception {
        if (object.getClass().getAnnotation(DBTable.class) == null) {
            throw new Exception("Vous devez annoter la classe " + object.getClass().getName() + " avec DBTable pour pouvoir effectuer les opérations !");
        }
        
        Field pk = GenericUtil.getPrimaryKeyField(object);
        String sql = "DELETE FROM " + object.getClass().getAnnotation(DBTable.class).name() + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String getFindQuery(Object object) throws Exception {
        if (object.getClass().getAnnotation(DBTable.class) == null) {
            throw new Exception("Vous devez annoter la classe " + object.getClass().getName() + " avec DBTable pour pouvoir effectuer les opérations !");
        }
        
        String whereClause = getWhereClause(object);
        String sql = "SELECT * FROM " + object.getClass().getAnnotation(DBTable.class).name() + " WHERE " + whereClause;
        return sql;
    }

    public static String getByIdQuery(Object object, Object id) throws Exception {
        if (object.getClass().getAnnotation(DBTable.class) == null) {
            throw new Exception("Vous devez annoter la classe " + object.getClass().getName() + " avec DBTable pour pouvoir effectuer les opérations !");
        }
        
        Field pk = GenericUtil.getPrimaryKeyField(object);
        String sql = "SELECT * FROM " + object.getClass().getAnnotation(DBTable.class).name() + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String getAllQuery(Object object, String suppl) throws Exception {
        if (object.getClass().getAnnotation(DBTable.class) == null) {
            throw new Exception("Vous devez annoter la classe " + object.getClass().getName() + " avec DBTable pour pouvoir effectuer les opérations !");
        }
        
        if (suppl == null) {
            suppl = "";
        }

        String sql = "SELECT * FROM " + object.getClass().getAnnotation(DBTable.class).name() + " " + suppl;
        return sql;
    }
}
