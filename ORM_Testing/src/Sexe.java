
import generalisation.annotations.DBField;
import generalisation.annotations.DBTable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author to
 */

@DBTable(name = "sexe", sequenceName = "seq_sexe")
public class Sexe {
    @DBField(name = "id_sexe", isPrimaryKey = true)
    int idSexe;
    
    @DBField(name = "sexe")
    String sexe;
    
    // Encapsulation
    public int getIdSexe() {
        return idSexe;
    }

    public void setIdSexe(int idSexe) {
        this.idSexe = idSexe;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    // Constructeur

    public Sexe() {
    }

    public Sexe(String sexe) {
        this.sexe = sexe;
    }


    public Sexe(int idSexe, String sexe) {
        this.idSexe = idSexe;
        this.sexe = sexe;
    }
    
}
