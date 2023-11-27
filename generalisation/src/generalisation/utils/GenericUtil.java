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
    public static int countDBField(Object object) throws Exception {
        int nb = 0;
        Field[] champs = object.getClass().getDeclaredFields();
        for (int i = 0; i < champs.length; i++) {
            if (champs[i].getAnnotation(DBField.class) != null) {
                nb++;
            }
        }
        return nb;
    }
    
    public static Field[] getDBField(Object object) throws Exception {
        Field[] champs = object.getClass().getDeclaredFields();
        Field[] results = new Field[countDBField(object)];
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
            System.out.println(detailObjectList(listes[i]));
        }
    }
    
    public static void detailList(List listes) throws Exception {
        System.out.println("Les elements du listes :");
        System.out.println("------------------------");
        for (int i = 0; i < listes.size(); i++) {
            System.out.println(detailObjectList(listes.get(i)));
        }
    }
    
    public static void detailObject(Object object) throws Exception {
        Field[] champs = object.getClass().getDeclaredFields();
        Object[] valeurs = parseGlobalObjectValue(object);
        if (valeurs.length == 0) {
            System.out.println(object.toString());
            return;
        }
        
        System.out.println("Detail de l'object : " + object);
        
        for (int i = 0; i < champs.length; i++) {
            System.out.println("- " + champs[i].getName() + " : " + valeurs[i]);
        }
    }
    
    public static String detailObjectList(Object object) throws Exception {
        Field[] champs = object.getClass().getDeclaredFields();
        Object[] valeurs = parseGlobalObjectValue(object);
            
        if (valeurs.length == 0) return object.toString();
        
        String resultat = "";
        for (int i = 0; i < champs.length; i++) {
            resultat += valeurs[i] + " | ";
        }
        return resultat.substring(0, resultat.length() - 3);
    }
    
    public static int getNextId(Object object, Connection connection) throws Exception {
        // Donne a un object la valeur de l'id avant save
        Statement statement = null;
        ResultSet resultat = null;
        try {
            statement = connection.createStatement();
            String sql = "SELECT nextval('" + object.getClass().getAnnotation(DBTable.class).sequenceName() + "')";
            resultat = statement.executeQuery(sql);
            resultat.next();
            
            int id = resultat.getInt(1);
            return id;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public static String prepareNextStringId(Object object, int newId) {
        String prefix = object.getClass().getAnnotation(DBTable.class).prefix();
        
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
    public static void giveObjectId(Object object, Connection connection) throws Exception {
        // Get the primary key field
        Field primaryKeyField = getPrimaryKeyField(object);
        if (primaryKeyField == null) return;
        String setterName = "set" + GenericUtil.firstUpperCase(primaryKeyField.getName());
        
        int id = getNextId(object, connection);
        
        
        // The setter for the primary key field
        Method setter = null;
        Object[] argument = new Object[1];
        
        // si int ou string comment faire
        if (primaryKeyField.getType() == int.class || primaryKeyField.getType() == Integer.class) {
            argument[0] = id;
            
            Class[] parametre = new Class[1];
            parametre[0] = primaryKeyField.getType();
            setter = object.getClass().getDeclaredMethod(setterName, parametre);
        } else if (primaryKeyField.getType() == String.class) {
            argument[0] = GenericUtil.prepareNextStringId(object, id);
            
            Class[] parametre = new Class[1];
            parametre[0] = primaryKeyField.getType();
            setter = object.getClass().getDeclaredMethod(setterName, parametre);
        } else {
            throw new Exception("Le cle primaire doit etre un entier ou un string !");
        }
        
        setter.invoke(object, argument);
    }
    
    // Set a value to a field using setter
    public static void setFieldValue(Object object, Field field, Object value) throws Exception {
        String setterName = "set" + GenericUtil.firstUpperCase(field.getName());
        
        // Get the setter
        Class[] parametre = {field.getType()};
        Method setter = object.getClass().getDeclaredMethod(setterName, parametre);
        
        // Invoke the setter
        Object[] argument = {value};
        setter.invoke(object, argument);
    }
    
    // Set object id manually
    public static void setObjectIdManually(Object object, Object id) throws Exception {
        // Get the primary key field
        Field primaryKeyField = getPrimaryKeyField(object);
        String setterName = "set" + GenericUtil.firstUpperCase(primaryKeyField.getName());
        
        // The setter for the primary key field
        Method setter = null;
        Object[] argument = new Object[1];
        
        // si int ou string comment faire
        if (primaryKeyField.getType() == int.class) {
            argument[0] = (int) id;
            
            Class[] parametre = new Class[1];
            parametre[0] = int.class;
            setter = object.getClass().getDeclaredMethod(setterName, parametre);
        } else if (primaryKeyField.getType() == String.class) {
            argument[0] = (String) id;
            
            Class[] parametre = new Class[1];
            parametre[0] = String.class;
            setter = object.getClass().getDeclaredMethod(setterName, parametre);
        } else {
            throw new Exception("Le cle primaire doit etre un entier ou un string !");
        }
        
        setter.invoke(object, argument);
    }
    
    public static Object[] parseObjectValue(Object object) throws Exception {
        // Return tous les valeurs des champs d'un object
        Field[] champs = GenericUtil.getDBField(object);
        Object[] objectValue = new Object[champs.length];
        for (int i = 0; i < champs.length; i++) {
            objectValue[i] = GenericUtil.getFieldValue(object, champs[i].getName());
        }
        return objectValue;
    }
    
    public static Object[] parseGlobalObjectValue(Object object) throws Exception {
        // Return tous les valeurs des champs d'un object
        Field[] champs = object.getClass().getDeclaredFields();
        Object[] objectValue = new Object[champs.length];
        
        for (int i = 0; i < champs.length; i++) {
            // Ne prends pas en comptes les attributs non modifiable
            if (!Modifier.isFinal(champs[i].getModifiers())) {
                objectValue[i] = GenericUtil.getFieldValue(object, champs[i].getName());
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
    
    public static Object getFieldValue(Object object, String fieldName) throws Exception {
        // Prends la valeur d'un champs
        String getter = "get" + GenericUtil.firstUpperCase(fieldName);
        Class[] parametres = new Class[0];
        Method method = object.getClass().getDeclaredMethod(getter, parametres);
        
        Object[] arguments = new Object[0];
        Object result = method.invoke(object, arguments);
        return result;
    }
    
    public static Field getPrimaryKeyField(Object object) throws Exception {
        // Trouve le champs cle primaire
        Field[] champs = GenericUtil.getDBField(object);
        for (int i = 0; i < champs.length; i++) {
            if (champs[i].getAnnotation(DBField.class).isPrimaryKey() == true) {
                return champs[i];
            }
        }
        throw new Exception("Aucun cle primaire trouvé dans " + object.getClass().getName());
    }
    
    public static Constructor getClassConstructor(Object object) throws Exception {
        // Prends le constructeur d'un object
        Field[] champs = GenericUtil.getDBField(object);
        
        Class[] parametres = new Class[champs.length];
        for(int i = 0; i < parametres.length; i++) {
            parametres[i] = champs[i].getType();
        }
        
        Constructor result = object.getClass().getConstructor(parametres);
        return result;
    }
    
    public static <T extends Object> T newEmptyObject(Class<T> objectClass) throws Exception {
        // Prends le constructeur d'un object
        Class[] parametres = new Class[0];
        Constructor construct = objectClass.getConstructor(parametres);
        
        Object[] arguments = new Object[0];
        T result = (T) construct.newInstance(arguments);
        return result;
    }
    
    
    
}
