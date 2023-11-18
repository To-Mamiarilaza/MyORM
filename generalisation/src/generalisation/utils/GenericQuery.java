/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.utils;

import generalisation.annotations.DBField;
import generalisation.annotations.DBTable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author to
 */
public class GenericQuery {

    // Ce classe est resposable de tous les constructions de requete
    public static String formatUpdateStatementValue(Object objet) throws Exception {
        Field[] champs = GenericUtil.getDBField(objet);

        String sql = "";
        for (int i = 0; i < champs.length; i++) {
            sql += champs[i].getAnnotation(DBField.class).name() + " = ?, ";
        }
        return sql.substring(0, sql.length() - 2);
    }

    public static String getUpdateQuery(Object objet, Object id) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(objet);
        String updateString = GenericQuery.formatUpdateStatementValue(objet);
        String sql = "UPDATE " + objet.getClass().getAnnotation(DBTable.class).name() + " SET " + updateString + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String getDeleteQuery(Object objet, Object id) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(objet);
        String sql = "DELETE FROM " + objet.getClass().getAnnotation(DBTable.class).name() + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String toDBFormat(Object objet) throws Exception {
        String resultat = null;

        if (objet == null) {
            resultat = "null";
        } else if (objet != null && objet.getClass().isAnnotationPresent(DBTable.class)) {
            Field pk = GenericUtil.getPrimaryKeyField(objet);
            resultat = "'" + GenericUtil.getFieldValue(objet, pk.getName()).toString() + "'";
        } else {
            resultat = "'" + objet.toString() + "'";
        }

        return resultat;
    }

    // Change the ? in the statement to the conveniable value
    public static void setAllStatementValue(PreparedStatement statement, Object objet) throws Exception {
        Field[] fields = GenericUtil.getDBField(objet);
        Object[] valeurs = GenericUtil.parseObjectValue(objet);

        for (int i = 0; i < valeurs.length; i++) {
            if (fields[i].getType() == int.class) {
                statement.setInt(i + 1, (int) valeurs[i]);
            } else if (fields[i].getType() == double.class) {
                statement.setDouble(i + 1, (double) valeurs[i]);
            } else if (fields[i].getType() == float.class) {
                statement.setFloat(i + 1, (float) valeurs[i]);
            } else if (fields[i].getType() == boolean.class) {
                statement.setBoolean(i + 1, (boolean) valeurs[i]);
            } else if (fields[i].getType() == String.class) {
                statement.setString(i + 1, (String) valeurs[i]);
            } else if (fields[i].getType() == Integer.class) {
                if (valeurs[i] == null) {
                    statement.setNull(i + 1, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(i + 1, (int) valeurs[i]);
                }
            } else if (fields[i].getType() == Double.class) {
                statement.setDouble(i + 1, (double) valeurs[i]);
            } else if (fields[i].getType() == Float.class) {
                statement.setFloat(i + 1, (float) valeurs[i]);
            } else if (fields[i].getType() == Boolean.class) {
                statement.setBoolean(i + 1, (boolean) valeurs[i]);
            } else if (fields[i].getType() == LocalDate.class) {
                Date dateValue = valeurs[i] == null ? null : Date.valueOf((LocalDate) valeurs[i]);
                statement.setDate(i + 1, dateValue);
            } else if (fields[i].getType() == LocalDateTime.class) {
                Timestamp timestampValue = valeurs[i] == null ? null : Timestamp.valueOf((LocalDateTime) valeurs[i]);
                statement.setTimestamp(i + 1, timestampValue);
            } else if (fields[i].getType() == LocalTime.class) {
                Time timeValue = valeurs[i] == null ? null : Time.valueOf((LocalTime) valeurs[i]);
                statement.setTime(i + 1, timeValue);
            } else if (fields[i].getAnnotation(DBField.class).isForeignKey()) {
                Field pk = GenericUtil.getPrimaryKeyField(valeurs[i]);
                Object foreignId = GenericUtil.getFieldValue(valeurs[i], pk.getName());
                statement.setObject(i + 1, foreignId);
            }
        }
    }

    public static String formatInsertPreparedStatementValue(Object objet) throws Exception {
        // Valeur d'un insert into
        Field[] fields = GenericUtil.getDBField(objet);
        String insertValue = "";
        for (int i = 0; i < fields.length; i++) {
            insertValue += "?, ";
        }
        insertValue = insertValue.substring(0, insertValue.length() - 2);
        return insertValue;
    }

    public static String getSaveQuery(Object objet) throws Exception {
        String preparedStatement = formatInsertPreparedStatementValue(objet);       // Prends tous les valeurs des champs de l'objet
        String sql = "INSERT INTO " + objet.getClass().getAnnotation(DBTable.class).name() + " VALUES (" + preparedStatement + ")";
        return sql;
    }

    public static String getByIdQuery(Object objet, Object id) throws Exception {
        Field pk = GenericUtil.getPrimaryKeyField(objet);
        String sql = "SELECT * FROM " + objet.getClass().getAnnotation(DBTable.class).name() + " WHERE " + pk.getAnnotation(DBField.class).name() + " = " + toDBFormat(id);
        return sql;
    }

    public static String getAllQuery(Object objet, String suppl) {
        if (suppl == null) {
            suppl = "";
        }

        String sql = "SELECT * FROM " + objet.getClass().getAnnotation(DBTable.class).name() + " " + suppl;
        return sql;
    }
}
