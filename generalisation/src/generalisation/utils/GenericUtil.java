/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalisation.utils;

import java.sql.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import generalisation.annotations.DBField;
import generalisation.annotations.DBTable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 *
 * @author to
 */
public class GenericUtil {
    public static int countDBField(Object objet) throws Exception {
        int nb = 0;
        Field[] champs = objet.getClass().getDeclaredFields();
        for (int i = 0; i < champs.length; i++) {
            if (champs[i].getAnnotation(DBField.class) != null) {
                nb++;
            }
        }
        return nb;
    }
    
    public static Field[] getDBField(Object objet) throws Exception {
        Field[] champs = objet.getClass().getDeclaredFields();
        Field[] results = new Field[countDBField(objet)];
        int indice = 0;
        for (int i = 0; i < champs.length; i++) {
            if (champs[i].getAnnotation(DBField.class) != null) {
                results[indice] = champs[i];
                indice++;
            }
        }
        return results;
    }
    
    public static <T extends Object> void detailTableau(T[] listes) throws Exception {
        System.out.println("Les elements du tableau :");
        System.out.println("------------------------");
        for (int i = 0; i < listes.length; i++) {
            System.out.println(detailObjetList(listes[i]));
        }
    }
    
    public static void detailList(List listes) throws Exception {
        System.out.println("Les elements du listes :");
        System.out.println("------------------------");
        for (int i = 0; i < listes.size(); i++) {
            System.out.println(detailObjetList(listes.get(i)));
        }
    }
    
    public static void detailObjet(Object objet) throws Exception {
        Field[] champs = objet.getClass().getDeclaredFields();
        Object[] valeurs = parseGlobalObjectValue(objet);
        if (valeurs.length == 0) {
            System.out.println(objet.toString());
            return;
        }
        
        System.out.println("Detail de l'objet : " + objet);
        
        for (int i = 0; i < champs.length; i++) {
            System.out.println("- " + champs[i].getName() + " : " + valeurs[i]);
        }
    }
    
    public static String detailObjetList(Object objet) throws Exception {
        Field[] champs = objet.getClass().getDeclaredFields();
        Object[] valeurs = parseGlobalObjectValue(objet);
            
        if (valeurs.length == 0) return objet.toString();
        
        String resultat = "";
        for (int i = 0; i < champs.length; i++) {
            resultat += valeurs[i] + " | ";
        }
        return resultat.substring(0, resultat.length() - 3);
    }
    
    public static int getNextId(Object objet, Connection connection) throws Exception {
        // Donne a un objet la valeur de l'id avant save
        Statement statement = null;
        ResultSet resultat = null;
        try {
            statement = connection.createStatement();
            String sql = "SELECT nextval('" + objet.getClass().getAnnotation(DBTable.class).sequenceName() + "')";
            resultat = statement.executeQuery(sql);
            resultat.next();
            
            int id = resultat.getInt(1);
            return id;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public static String prepareNextStringId(Object objet, int newId) {
        String prefix = objet.getClass().getAnnotation(DBTable.class).prefix();
        
        int zeroNumber = 4;
        String newIdString = String.valueOf(newId);
        
        int newZero = zeroNumber - newIdString.length();
        String zeroResult = "";
        
        while (newZero > 0) {
            zeroResult += "0";
            newZero--;
        }
        
        return prefix + zeroResult + newId;
    }
    
    // Get the next sequence and give it to the new Object
    public static void giveObjectId(Object objet, Connection connection) throws Exception {
        // Get the primary key field
        Field primaryKeyField = getPrimaryKeyField(objet);
        if (primaryKeyField == null) return;
        String setterName = "set" + GenericUtil.firstUpperCase(primaryKeyField.getName());
        
        int id = getNextId(objet, connection);
        
        
        // The setter for the primary key field
        Method setter = null;
        Object[] argument = new Object[1];
        
        // si int ou string comment faire
        if (primaryKeyField.getType() == int.class) {
            argument[0] = id;
            
            Class[] parametre = new Class[1];
            parametre[0] = int.class;
            setter = objet.getClass().getDeclaredMethod(setterName, parametre);
        } else if (primaryKeyField.getType() == String.class) {
            argument[0] = GenericUtil.prepareNextStringId(objet, id);
            
            Class[] parametre = new Class[1];
            parametre[0] = String.class;
            setter = objet.getClass().getDeclaredMethod(setterName, parametre);
        } else {
            throw new Exception("Le cle primaire doit etre un entier ou un string !");
        }
        
        setter.invoke(objet, argument);
    }
    
    // Set object id manually
    public static void setObjectIdManually(Object objet, Object id) throws Exception {
        // Get the primary key field
        Field primaryKeyField = getPrimaryKeyField(objet);
        String setterName = "set" + GenericUtil.firstUpperCase(primaryKeyField.getName());
        
        // The setter for the primary key field
        Method setter = null;
        Object[] argument = new Object[1];
        
        // si int ou string comment faire
        if (primaryKeyField.getType() == int.class) {
            argument[0] = (int) id;
            
            Class[] parametre = new Class[1];
            parametre[0] = int.class;
            setter = objet.getClass().getDeclaredMethod(setterName, parametre);
        } else if (primaryKeyField.getType() == String.class) {
            argument[0] = (String) id;
            
            Class[] parametre = new Class[1];
            parametre[0] = String.class;
            setter = objet.getClass().getDeclaredMethod(setterName, parametre);
        } else {
            throw new Exception("Le cle primaire doit etre un entier ou un string !");
        }
        
        setter.invoke(objet, argument);
    }
    
    public static Object[] parseObjectValue(Object objet) throws Exception {
        // Return tous les valeurs des champs d'un objet
        Field[] champs = GenericUtil.getDBField(objet);
        Object[] objectValue = new Object[champs.length];
        for (int i = 0; i < champs.length; i++) {
            objectValue[i] = GenericUtil.getFieldValue(objet, champs[i].getName());
        }
        return objectValue;
    }
    
    public static Object[] parseGlobalObjectValue(Object objet) throws Exception {
        // Return tous les valeurs des champs d'un objet
        Field[] champs = objet.getClass().getDeclaredFields();
        Object[] objectValue = new Object[champs.length];
        
        for (int i = 0; i < champs.length; i++) {
            // Ne prends pas en comptes les attributs non modifiable
            if (!Modifier.isFinal(champs[i].getModifiers())) {
                objectValue[i] = GenericUtil.getFieldValue(objet, champs[i].getName());
            }
        }
        return objectValue;
    }
    
    public static String firstUpperCase(String word) {
        String first = word.substring(0, 1);
        String over = word.substring(1, word.length());
        first = first.toUpperCase();
        
        return first + over;
    }
    
    public static Object getFieldValue(Object objet, String fieldName) throws Exception {
        // Prends la valeur d'un champs
        String getter = "get" + GenericUtil.firstUpperCase(fieldName);
        Class[] parametres = new Class[0];
        Method method = objet.getClass().getDeclaredMethod(getter, parametres);
        
        Object[] arguments = new Object[0];
        Object result = method.invoke(objet, arguments);
        return result;
    }
    
    public static Field getPrimaryKeyField(Object objet) throws Exception {
        // Trouve le champs cle primaire
        Field[] champs = GenericUtil.getDBField(objet);
        for (int i = 0; i < champs.length; i++) {
            if (champs[i].getAnnotation(DBField.class).isPrimaryKey() == true) {
                return champs[i];
            }
        }
        return null;
    }
    
    public static Constructor getClassConstructor(Object objet) throws Exception {
        // Prends le constructeur d'un objet
        Field[] champs = GenericUtil.getDBField(objet);
        
        Class[] parametres = new Class[champs.length];
        for(int i = 0; i < parametres.length; i++) {
            parametres[i] = champs[i].getType();
        }
        
        Constructor result = objet.getClass().getConstructor(parametres);
        return result;
    }
    
    public static <T extends Object> T newEmptyObject(Class<T> objectClass) throws Exception {
        // Prends le constructeur d'un objet
        Class[] parametres = new Class[0];
        Constructor construct = objectClass.getConstructor(parametres);
        
        Object[] arguments = new Object[0];
        T result = (T) construct.newInstance(arguments);
        return result;
    }
    
    
    
}
