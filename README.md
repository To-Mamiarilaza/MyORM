Bonjour a tous !
Voici une petite guide d'utilisation de cette librarie ORM

# MANUELLE D'UTILISATION

1. Integration dans un projet

Pour integrer l' ORM dans votre projet il faut prendre le generalisation.jar dans le dossier generalisation et l' introduire en tant que dépendance de votre projet.
Ensuite, on doit crée un classe "connection.DBConnection" avec une fonction " public static Connection getConnection() throws Exception " qui retourne
un java.sql.Connection pour que l'ORM puisse prendre le connection en fonction de vos paramètres.

2. Mapping de classe et table

- Lors du création de vos table si vous souhaitez incrementer automatiquement : 
	- Metter le type en SERIAL ou AUTO INCREMENT si on choisit d'utiliser l'ID en integer	
	- Mais si on veut utiliser un VARCHAR comme ID, il faut crée un sequence et un prefix pour l'ID de preference

- Voila la liste des types supporté autre que les Classe foreign key :
    . Integer <--> INTEGER
    . Double <--> DOUBLE PRECISION
    . Float <--> FLOAT
    . BigDecimal <--> DECIMAL(x, y)
    . String <--> VARCHAR
    . sql.Date ou time.LocalDate <--> Date
    . sql.Timestamp ou time.LocalDateTime <--> Timestamp ou DateTime

- Pour le moment l' ORM supporte uniquement un seule primary key qui soit un integer ou un varchar.

- Dans le classe de mapping on doit annoter la classe avec @DBTable
    Les attributs de @DBTable :
    - name : C'est le nom de la table correspondant au classe
    - sequenceName : C'est le nom du séquence associé si on veut associer a un séquence
    - prefix : C'est le préfix ajouter avant le séquence si le primary key est un varchar
    - autoIncrement : Si on veut que la valeur de l'ID s'incrémente automatiquement ( ID Integer uniquement )

- Ensuite, les attributs doivent suivre ces quelques règles :
    - L'arrangement des attributs dans la classe doit être pour le moment le même qu'avec les colonnes du tables
    - Les attributs pris en compte lors du mapping doivent être annoté avec @DBField
    Les attributs de @DBField :
    - name : le nom du colonne correspondant a l'attribut dans le table
    - isPrimaryKey : boolean , si oui c'est un primary key ( on ne supporte pas encore plus d'un primary key)
    - isForeignKey : boolean, pour dire qu'un cette champ est un clé etranger d'un autre table
    - getChild : boolean, lors d'un SELECT d'un objet, si un champ est foreign key nous allons prendre ce champ continuellement si on 
    spécifie que getChild = true, c'est a dire pour ce champ prendre les objets en foreign key

- Apres, On doit crée des getters et setters pour chacun des attributs. Et ils doivent avoir le format "set" ou "get" + "NomDuField". # Avec le premier lettre en majuscule
  
- Enfin, un constructeur vide est requis pour permettre la reflection

3. Connection vers la base de données
	Pour la connection, nous devons ajouter un fichier connection.properties dans le dossier contenant les classes, elle contient :
	. datasource.url=connectionString ( end with "/" )
	. datasource.database=databaseName
	. datasource.username=user
	. datasource.password=password
	. datasource.type=databaseType

	Ensuite l'ORM va prendre les informations de connection dans ce fichier et gérer la connection pour vous,
	mais si vous avez besoin de l'objet connection vous pouvez l'avoir par la method GenericDAO.getConnection()

4. Les fonctions CRUD
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

5. Les évolutions à faire
    - Pour le moment cette ORM supporte uniquement POSTGRES mais la fonctionnalités multibase de données est en développement
    - Le fait de devoir ordonnée les attributs selon la colonne des tables peut être frustrant a long terme, on doit changer la formation du requette
    - pagination, calcul généraliser, import CSV
