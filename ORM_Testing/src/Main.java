
import generalisation.GenericDAO.GenericDAO;
import generalisation.utils.GenericUtil;
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
        // Person person = new Person("ANDRIANJAFY", "Christine", new Sexe(2, "Femme"), LocalDate.parse("2000-01-12"));
        // GenericDAO.save(person, null);
        
        /// Pour avoir la liste de tous les persons
        /// La deuxieme argument 'suppl' est utilisée si nous voulons ajouter de requete supplementaire comme WHERE ou ORDER BY ...
        //List<Person> persons = GenericDAO.getAll(Person.class, null, null);
        // GenericUtil.detailList(persons);
        
        /// Modifier une personne ayant un ID donnée par le nouvelle objet donnée
        
        // Person modifyPerson = new Person("PER0001", "RAKOTONDRAZEFA", "Maminirina", new Sexe(1, "Homme"), LocalDate.parse("1974-07-31"));
        // GenericDAO.updateById(modifyPerson, "PER0001", null);
        
        /// Supprimer une entite chien par son ID
        // GenericUtil.detailList(GenericDAO.getAll(Chien.class, null, null));
        // GenericDAO.deleteById(Chien.class, "CHE0003", null);
        
        /// Pour faire un requete directe et personnalisé : " Les chiens ayant un prix superieur a ... "
        // List<Chien> chiens = GenericDAO.directQuery(Chien.class, "SELECT * FROM chien WHERE price > 200000", null);
        // GenericUtil.detailList(chiens);
        
        /// Pour faire une update directe et personnalisable
        // GenericDAO.directUpdate("UPDATE chien SET price = 50000", null);
    }
}
