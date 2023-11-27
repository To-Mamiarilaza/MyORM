
import generalisation.genericDAO.GenericDAO;
import generalisation.utils.GenericUtil;
import java.lang.reflect.Field;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author to
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // On crée notre propre connection si nous voulons faire un transaction
        
        /// Ajout d'un nouveau person
        // Person person = new Person("New person", "firstname", new Sexe(2, "Femme"), java.sql.Date.valueOf(LocalDate.now()));
        // GenericDAO.save(person);
        
        /// Pour avoir la liste de tous les persons
        /// La deuxieme argument 'suppl' est utilisée si nous voulons ajouter de requete supplementaire comme WHERE ou ORDER BY ...
        // List<Person> persons = GenericDAO.getAll(Person.class);
        // GenericUtil.detailList(persons);
        
        /// Modifier une personne ayant un ID donnée par le nouvelle objet donnée
        
        // Person modifyPerson = new Person();
        // modifyPerson.setIdPerson("PER0002");
        // modifyPerson.setName("Modified Person");
        // modifyPerson.setFirstname("His firstname");
        // modifyPerson.setDateNaissance(Date.valueOf(LocalDate.now()));
        
        // GenericDAO.updateBydId(modifyPerson);
        
        /// Supprimer une entite chien par son ID
        // GenericUtil.detailList(GenericDAO.getAll(Chien.class, null, null));
        // GenericDAO.deleteById(Person.class, "PER0036");
        
        /// Pour faire un requete directe et personnalisé : " Les chiens ayant un prix superieur a ... "
        // List<Chien> chiens = GenericDAO.directQuery(Chien.class, "SELECT * FROM chien WHERE price > 3000");
        // GenericUtil.detailList(chiens);
        
        /// Pour faire une update directe et personnalisable
        // GenericDAO.directUpdate("UPDATE chien SET price = 50000", null);
    }
}
