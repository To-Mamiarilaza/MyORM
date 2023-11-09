Bonjour a tous !
Je suis MAMIARILAZA To Niasimandimby, Etudiant en troisième année à l' IT University.
Je vais vous décrire de manière simple le manuelle d'utilisation de mon ORM. Celle ci
fonctionne principalement par reflexion

# MANUELLE D'UTILISATION

1. Integration dans un projet

Pour integrer l' ORM dans votre projet il faut prendre le generalisation.jar dans le dossier generalisation et l' introduire en tant que dépendance de votre projet.
Ensuite, on doit crée un classe "connection.DBConnection" avec une fonction " public static Connection getConnection() throws Exception " qui retourne
un java.sql.Connection pour que l'ORM puisse prendre le connection en fonction de vos paramètres.

2. Mapping de classe et table

- Lors du création de vos table les primary key qui doivent se générer automatiquement doivent être associé avec un séquence. Donc pour chaque table on doit crée des séquences.
- Voila la liste des types supporté autre que les Classe foreign key :
    . int, Integer
    . double, Double
    . float, Float
    . boolean, Boolean
    . String
    . LocalDate
    . LocalDateTime
    . LocalTime

- Pour le moment l' ORM supporte uniquement un seule primary key qui soit un integer ou un varchar.

- Dans le classe de mapping on doit annoter la classe avec @DBTable
    Les attributs de @DBTable :
    - name : C'est le nom de la table correspondant au classe
    - sequenceName : C'est le nom du séquence associé
    - prefix : C'est le préfix ajouter avant le séquence si le primary key est un varchar

- Ensuite, les attributs doivent suivre ces quelques règles :
    - L'arrangement des attributs dans la classe doit être pour le moment le même qu'avec les colonnes du tables
    - Et de même les arguments du constructeur doit être arrangé de même que les attributs du classe

    - Les attributs pris en compte lors du mapping doivent être annoté avec @DBField
    Les attributs de @DBField :
    - name : le nom du colonne correspondant a l'attribut dans le table
    - isPrimaryKey : boolean , si oui c'est un primary key ( on ne supporte pas encore plus d'un primary key)
    - isForeignKey : boolean, pour dire qu'un cette champ est un clé etranger d'un autre table
    - getChild : boolean, lors d'un SELECT d'un objet, si un champ est foreign key nous allons prendre ce champ continuellement si on 
    spécifie que getChild = true, c'est a dire pour ce champ prendre les objets en foreign key

- Apres, On doit crée des getters et setters pour chacun des attributs. Et ils doivent avoir le format "set" ou "get" + "NomDuField". # Avec le premier lettre en majuscule
  
- Enfin, au niveau du constructeur :
    - On doit crée trois constructeur par defaut :
        1. Un constructeur vide
        2. Un constructeur avec tous les attributs pris en charge dans le mapping bien ordonnée
        3. Un constructeur avec tous les attributs sauf le primary key qui va être généré

3. Les fonctions CRUD
    Pour chaque fonction , le paramètre connection doit être fourni si on veut faire un transaction sinon on passe null.

    Dans le main du projet de test il y a un exemple chaqun des fonctions, mais je vais resumé un peu :

    - GenericDAO.save(objet a sauvegardé, connection) --> void
    
    - GenericDAO.getAll(classe de l'objet a prendre, supplementaire, connection) --> List<Objet en question>    # Le supplementaire c'est pour personnalisé la requette sinon on passe null

    - GenericDAO.findById(classe de l'objet, objet id, connection) --> L' objet en question

    - GenericDAO.updateById(nouvelle objet qui va remplacé, id du ligne a remplacé, connection) --> void

    - GenericDAO.deleteById(classe de l'objet, id de l'objet a supprimé, connection) --> void

    - GenericDAO.directQuery(Classe de l'objet qui va se servir de mapping, SQL a executer, connection) --> List<Objet de mapping>

    - GenericDAO.directUpdate(SQL a executer, connection) --> void      # Update avec un SQL directe et personnalisable

    - GenericUtil.detailList(List<Object>) --> Affiche en console le detail d'une liste d'objet

    - GenericUtil.detailObjet(Object) --> Affiche en console le detail d'une objet

4. Les évolutions à faire
    - Cette ORM n'est pas encore multibase de données, le problème c'est au niveau du prise de la valeur next du sequence
    - Les dates supportés sont seulement les types java.time....
    - Le fait de devoir ordonnée les attributs selon la colonne des tables peut être frustrant a long terme, on doit changer la formation du requette
    - Le boucle de formatage peut être encore optimisé
    - On peut peut etre ne plus être forcé de crée des getters et setters et constructeurs
 