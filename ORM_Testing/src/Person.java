
import generalisation.annotations.DBField;
import generalisation.annotations.DBTable;
import java.time.LocalDate;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author To Mamiarilaza
 */

@DBTable(name = "person", sequenceName = "seq_person", prefix = "PER")
public class Person {
    
    /// Field
    @DBField(name="id_person", isPrimaryKey = true)
    String idPerson;
    
    @DBField(name = "name")
    String name;
    
    
    @DBField(name = "firstname")
    String firstname;
    
    @DBField(name = "id_sexe", isForeignKey = true)
    Sexe sexe;
    
    @DBField(name = "date_naissance")
    Date dateNaissance;
    
    List<Chien> chiens;
    
    /// Getter and setter

    public String getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(String idPerson) {
        this.idPerson = idPerson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public List<Chien> getChiens() {
        return chiens;
    }

    public void setChiens(List<Chien> chiens) {
        this.chiens = chiens;
    }
    
    public Person() {
    }
    
}
