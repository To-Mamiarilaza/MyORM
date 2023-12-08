
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
@DBTable(name = "chien", autoIncrement = true)
public class Chien {
    /// Field
    @DBField(name = "id_chien", isPrimaryKey = true)
    Integer idChien;
    
    @DBField(name = "id_person", isForeignKey = true, getChild = false)
    Person person;
    
    @DBField(name = "name")
    String name;
    
    @DBField(name = "price")
    Double price;
    
    /// Getter and setter

    public Integer getIdChien() {
        return idChien;
    }

    public void setIdChien(Integer idChien) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /// Constructor

    public Chien(Integer idChien, Person person, String name, Double price) {
        this.idChien = idChien;
        this.person = person;
        this.name = name;
        this.price = price;
    }

    public Chien(Person person, String name, Double price) {
        this.person = person;
        this.name = name;
        this.price = price;
    }

    public Chien() {
    }
    
}
