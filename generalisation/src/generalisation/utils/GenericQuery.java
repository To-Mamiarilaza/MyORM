/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.utils;

import generalisation.annotations.DBField;
import generalisation.annotations.DBTable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Date;

/**
 *
 * @author to
 */
public class GenericQuery {

    // Ce classe est resposable de tous les constructions de requete
    public static String formatUpdateStatementValue(Object object) throws Exception {
        Field[] champs = GenericUtil.getDBField(object);

        String sql = "";
        for (int i = 0; i < champs.length; i++) {
            sql += champs[i].getAnnotation(DBField.class).name() + " = ?, ";
        }
        return sql.substring(0, sql.length() - 2);
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

    public static void setAllStatementValue(PreparedStatement statement, Object object) throws Exception {
        Field[] fields = GenericUtil.getDBField(object);
        Object[] valeurs = GenericUtil.parseObjectValue(object);

        for (int i = 0; i < valeurs.length; i++) {
            if (fields[i].getAnnotation(DBField.class).isForeignKey()) {
                if (valeurs[i] != null) {
                    Field pk = GenericUtil.getPrimaryKeyField(valeurs[i]);
                    Object foreignId = GenericUtil.getFieldValue(valeurs[i], pk.getName());
                    statement.setObject(i + 1, foreignId);
                } else {
                    statement.setObject(i + 1, null);
                }

            } else {
                try {
                    statement.setObject(i + 1, valeurs[i]);
                } catch (Exception e) {
                    System.out.println("Un problème survient au niveau de la colonne : " + fields[i].getType());
                    throw e;
                }
            }
        }
    }

    public static String formatInsertPreparedStatementValue(Object object) throws Exception {
        // Valeur d'un insert into
        Field[] fields = GenericUtil.getDBField(object);
        String insertValue = "";
        for (int i = 0; i < fields.length; i++) {
            insertValue += "?, ";
        }
        insertValue = insertValue.substring(0, insertValue.length() - 2);
        return insertValue;
    }

    public static String getSaveQuery(Object object) throws Exception {
        String preparedStatement = formatInsertPreparedStatementValue(object);       // Prends tous les valeurs des champs de l'object
        String sql = "INSERT INTO " + object.getClass().getAnnotation(DBTable.class).name() + " VALUES (" + preparedStatement + ")";
        return sql;
    }

    public static String getUpdateQuery(Object object, Object id) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(object);
        String updateString = GenericQuery.formatUpdateStatementValue(object);
        String sql = "UPDATE " + object.getClass().getAnnotation(DBTable.class).name() + " SET " + updateString + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String getDeleteQuery(Object object, Object id) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(object);
        String sql = "DELETE FROM " + object.getClass().getAnnotation(DBTable.class).name() + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String getByIdQuery(Object object, Object id) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(object);
        String sql = "SELECT * FROM " + object.getClass().getAnnotation(DBTable.class).name() + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String getAllQuery(Object object, String suppl) {
        if (suppl == null) {
            suppl = "";
        }

        String sql = "SELECT * FROM " + object.getClass().getAnnotation(DBTable.class).name() + " " + suppl;
        return sql;
    }
}
