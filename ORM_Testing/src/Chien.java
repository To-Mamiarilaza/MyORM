
import generalisation.annotations.DBField;
import generalisation.annotations.DBTable;
import java.time.LocalTime;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author To Mamiarilaza
 */
@DBTable(name = "chien", sequenceName = "seq_chien", prefix = "CHN")
public class Chien {
    /// Field
    @DBField(name = "id_chien", isPrimaryKey = true)
    String idChien;
    
    @DBField(name = "id_person", isForeignKey = true)
    Person person;
    
    @DBField(name = "name")
    String name;
    
    @DBField(name = "price")
    double price;
    
    /// Getter and setter

    public String getIdChien() {
        return idChien;
    }

    public void setIdChien(String idChien) {
        this.idChien = idChien;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /// Constructor

    public Chien(String idChien, Person person, String name, double price) {
        this.idChien = idChien;
        this.person = person;
        this.name = name;
        this.price = price;
    }

    public Chien(Person person, String name, double price) {
        this.person = person;
        this.name = name;
        this.price = price;
    }

    public Chien() {
    }
    
}
