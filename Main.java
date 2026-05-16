package shelter;

import shelter.exceptions.InvalidPetOperationException;
import shelter.exceptions.PetRanAwayException;
import shelter.model.*;
import shelter.service.ShelterFileManager;
import shelter.service.ShelterManager;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Main {

    private static final String FILE_PATH  = "shelter_data.csv";
    private static final String PROPS_PATH = "shelter_stats.properties";

    private static final ShelterManager     manager     = new ShelterManager();
    private static final ShelterFileManager fileManager = new ShelterFileManager();
    private static final Scanner            scanner     = new Scanner(System.in);

    private static int nextId = 1;

    public static void main(String[] args) {
        printBanner();
        loadOnStartup();   // Chargement automatique du fichier CSV au démarrage

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Votre choix : ");

            switch (choice) {
                case 1  -> menuAjouter();
                case 2  -> menuNourrir();
                case 3  -> menuJouer();
                case 4  -> menuSoigner();
                case 5  -> menuAdopter();
                case 6  -> menuAfficher();
                case 7  -> menuSupprimer();
                case 8  -> menuCycle();
                case 9  -> menuSauvegarder();
                case 10 -> menuCharger();
                case 11 -> menuStatsMap();
                case 0  -> {
                    saveOnExit();
                    System.out.println("\n🐾 Merci d'avoir pris soin de nos animaux. À bientôt !\n");
                    running = false;
                }
                default -> System.out.println("⚠️  Option invalide. Choisissez entre 0 et 11.\n");
            }
        }

        scanner.close();
    }


    /**
     * Menu 1 – Ajouter un animal.
     * Permet de créer un Chien, Chat ou Perroquet avec ses attributs spécifiques.
     * Gère InvalidPetOperationException si l'ID est déjà utilisé.
     */
    private static void menuAjouter() {
        printSectionHeader("➕ AJOUTER UN ANIMAL");

        System.out.println("  Type : 1) Chien   2) Chat   3) Perroquet");
        int type = readInt("Votre choix : ");
        if (type < 1 || type > 3) {
            System.out.println("⚠️  Type invalide.\n");
            return;
        }

        String name = readString("Nom de l'animal : ");
        int    age  = readIntPositive("Âge (en années) : ");
        int    id   = nextId++;   // ID attribué automatiquement

        try {
            Animal animal = switch (type) {
                case 1 -> {
                    String  breed   = readString("Race du chien : ");
                    boolean trained = readBoolean("Est-il dressé ? (o/n) : ");
                    Dog dog = new Dog(id, name, age, breed);
                    dog.setTrained(trained);
                    yield dog;
                }
                case 2 -> {
                    boolean longHair = readBoolean("A-t-il des poils longs ? (o/n) : ");
                    int indep = readIntRange("Niveau d'indépendance (0=câlin, 100=indép.) : ", 0, 100);
                    yield new Cat(id, name, age, longHair, indep);
                }
                case 3 -> {
                    String  color  = readString("Couleur du plumage : ");
                    boolean canFly = readBoolean("Peut-il voler ? (o/n) : ");
                    yield new Parrot(id, name, age, color, canFly);
                }
                default -> throw new InvalidPetOperationException("Type inconnu.", "BAD_TYPE");
            };

            manager.addPet(animal);

            // Apprentissage de mots pour le perroquet
            if (animal instanceof Parrot parrot) {
                System.out.println("💬 Voulez-vous lui apprendre des mots maintenant ?");
                if (readBoolean("(o/n) : ")) {
                    String word = readString("Mot à apprendre : ");
                    parrot.learnWord(word);
                }
            }

        } catch (InvalidPetOperationException e) {
            System.err.println("❌ Erreur : " + e);
            nextId--;   // on annule l'incrémentation si l'ajout a échoué
        }
        System.out.println();
    }


    private static void menuNourrir() {
        printSectionHeader("🍖 NOURRIR UN ANIMAL");
        int id = readInt("ID de l'animal : ");
        try {
            manager.feedPet(id);
        } catch (InvalidPetOperationException e) {
            System.err.println("❌ " + e + "\n");
        }
        System.out.println();
    }


    private static void menuJouer() {
        printSectionHeader("🎾 JOUER AVEC UN ANIMAL");
        int id = readInt("ID de l'animal : ");
        try {
            manager.playWithPet(id);
            Animal a = manager.findById(id);
            if (a != null) a.makeSound();
        } catch (InvalidPetOperationException e) {
            System.err.println("❌ " + e + "\n");
        }
        System.out.println();
    }


    private static void menuSoigner() {
        printSectionHeader("💊 SOIGNER UN ANIMAL");
        int id = readInt("ID de l'animal : ");
        try {
            manager.healPet(id);
            Animal a = manager.findById(id);
            if (a != null) offerSpecialAction(a);
        } catch (InvalidPetOperationException e) {
            System.err.println("❌ " + e + "\n");
        }
        System.out.println();
    }


    private static void menuAdopter() {
        printSectionHeader("🏠 ADOPTER UN ANIMAL");
        manager.listAdoptableAnimals();
        int    id   = readInt("ID de l'animal à adopter : ");
        String name = readString("Votre nom (adoptant) : ");
        try {
            manager.adoptPet(id, name);
        } catch (InvalidPetOperationException e) {
            System.err.println("❌ " + e + "\n");
        }
        System.out.println();
    }

    private static void menuAfficher() {
        printSectionHeader("📋 AFFICHER LES ANIMAUX");
        System.out.println("  1) Liste complète");
        System.out.println("  2) Fiche détaillée d'un animal");
        System.out.println("  3) Animaux disponibles à l'adoption");
        System.out.println("  4) Rechercher par nom");
        int sub = readInt("Votre choix : ");

        switch (sub) {
            case 1 -> manager.listPets();
            case 2 -> {
                int id = readInt("ID de l'animal : ");
                manager.displayPetStats(id);
            }
            case 3 -> manager.listAdoptableAnimals();
            case 4 -> {
                String query = readString("Partie du nom : ");
                List<Animal> results = manager.findByName(query);
                if (results.isEmpty()) {
                    System.out.println("Aucun animal trouvé pour « " + query + " ».");
                } else {
                    System.out.println("Résultats (" + results.size() + ") :");
                    for (Animal a : results) {
                        System.out.println("  [" + a.getId() + "] " + a.getName()
                                + " – " + a.getSpecies());
                    }
                }
            }
            default -> System.out.println("⚠️  Sous-option invalide.");
        }
        System.out.println();
    }


    private static void menuSupprimer() {
        printSectionHeader("🗑️  SUPPRIMER UN ANIMAL");
        int id = readInt("ID de l'animal à retirer du refuge : ");

        // Vérification préalable : affiche la fiche avant suppression
        Animal a = manager.findById(id);
        if (a == null) {
            System.out.println("⚠️  Aucun animal avec l'ID " + id + ".\n");
            return;
        }
        System.out.println("Vous allez supprimer : " + a.getName() + " (" + a.getSpecies() + ")");
        if (!readBoolean("Confirmer ? (o/n) : ")) {
            System.out.println("Suppression annulée.\n");
            return;
        }

        try {
            manager.removePet(id);
        } catch (InvalidPetOperationException e) {
            System.err.println("❌ " + e + "\n");
        }
        System.out.println();
    }


    private static void menuCycle() {
        printSectionHeader("⏰ AVANCER D'UN CYCLE DE TEMPS");
        System.out.println("La faim augmente, l'humeur peut baisser...");
        System.out.println("Un animal trop négligé pourrait s'enfuir !\n");
        manager.updateAllStates();
    }


    private static void menuSauvegarder() {
        printSectionHeader("💾 SAUVEGARDER LES DONNÉES");
        try {
            // Récupérer la liste des animaux via findByName("") = tous
            List<Animal> all = manager.findByName("");
            fileManager.saveToFile(all, FILE_PATH);
            fileManager.saveToProperties(all, PROPS_PATH);
            System.out.println("✅ Sauvegarde complète réussie.\n");
        } catch (IOException e) {
            System.err.println("❌ Erreur de sauvegarde : " + e.getMessage() + "\n");
        }
    }


    private static void menuCharger() {
        printSectionHeader("📂 CHARGER LES DONNÉES");
        System.out.println("⚠️  Cette opération remplace les données actuelles en mémoire.");
        if (!readBoolean("Continuer ? (o/n) : ")) {
            System.out.println("Chargement annulé.\n");
            return;
        }
        try {
            List<Animal> loaded = fileManager.loadFromFile(FILE_PATH);
            // Supprimer tous les animaux actuels avant de recharger
            List<Animal> current = manager.findByName("");
            for (Animal a : current) {
                try { manager.removePet(a.getId()); }
                catch (InvalidPetOperationException ignored) {}
            }
            // Réinitialiser le compteur d'ID
            nextId = 1;
            for (Animal a : loaded) {
                try {
                    manager.addPet(a);
                    if (a.getId() >= nextId) nextId = a.getId() + 1;
                } catch (InvalidPetOperationException e) {
                    System.err.println("⚠️  Animal ignoré au chargement : " + e.getMessage());
                }
            }
            System.out.println("✅ " + loaded.size() + " animal(aux) chargé(s).\n");
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement : " + e.getMessage() + "\n");
        }
    }


    private static void menuStatsMap() {
        printSectionHeader("📊 STATS RAPIDES (HashMap)");
        List<Animal> all = manager.findByName("");
        if (all.isEmpty()) {
            System.out.println("Aucun animal dans le refuge.\n");
            return;
        }
        fileManager.displayStatsMap(all);
    }


    private static void offerSpecialAction(Animal animal) {
        if (animal instanceof Dog dog) {
            System.out.println("🐕 Action spéciale disponible : dresser " + dog.getName());
            if (readBoolean("Dresser le chien ? (o/n) : ")) {
                dog.train();
            }
        } else if (animal instanceof Cat cat) {
            System.out.println("🐈 Action spéciale : brosser ou faire ronronner " + cat.getName());
            System.out.println("  1) Brosser   2) Faire ronronner");
            int sub = readInt("Choix : ");
            if (sub == 1)      cat.groom();
            else if (sub == 2) cat.purr();
        } else if (animal instanceof Parrot parrot) {
            System.out.println("🦜 Action spéciale : apprendre un mot à " + parrot.getName());
            System.out.println("Mots connus : " + parrot.getLearnedWordsAsString());
            if (readBoolean("Lui apprendre un nouveau mot ? (o/n) : ")) {
                String word = readString("Mot : ");
                parrot.learnWord(word);
            }
        }
    }




    private static void loadOnStartup() {
        try {
            List<Animal> loaded = fileManager.loadFromFile(FILE_PATH);
            for (Animal a : loaded) {
                try {
                    manager.addPet(a);
                    if (a.getId() >= nextId) nextId = a.getId() + 1;
                } catch (InvalidPetOperationException e) {
                    System.err.println("⚠️  Animal ignoré : " + e.getMessage());
                }
            }
            if (!loaded.isEmpty())
                System.out.println("✅ Données précédentes restaurées (" + loaded.size() + " animaux).\n");
        } catch (IOException e) {
            // Fichier absent = première exécution, on peuple avec des exemples
            System.out.println("ℹ️  Aucun fichier de données trouvé. Démarrage avec des animaux d'exemple.\n");
            loadSampleData();
        }
    }


    private static void saveOnExit() {
        try {
            List<Animal> all = manager.findByName("");
            fileManager.saveToFile(all, FILE_PATH);
            System.out.println("💾 Données sauvegardées automatiquement.");
        } catch (IOException e) {
            System.err.println("⚠️  Sauvegarde automatique échouée : " + e.getMessage());
        }
    }


    private static void loadSampleData() {
        try {
            Dog    rex    = new Dog(nextId++, "Rex",     3, "Berger Allemand");
            rex.setTrained(true);

            Cat    minou  = new Cat(nextId++, "Minou",   5, true, 40);

            Parrot coco   = new Parrot(nextId++, "Coco", 2, "vert", true);
            coco.learnWord("bonjour");
            coco.learnWord("biscuit");

            Dog    luna   = new Dog(nextId++, "Luna",    1, "Labrador");
            Cat    felix  = new Cat(nextId++, "Felix",   7, false, 75);

            manager.addPet(rex);
            manager.addPet(minou);
            manager.addPet(coco);
            manager.addPet(luna);
            manager.addPet(felix);

            System.out.println("🐾 5 animaux d'exemple ajoutés au refuge.\n");
        } catch (InvalidPetOperationException e) {
            System.err.println("Erreur lors du chargement des exemples : " + e);
        }
    }


    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║      🐾   HAPPY PAWS SHELTER   🐾                   ║");
        System.out.println("║      Refuge virtuel pour animaux abandonnés          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│              MENU PRINCIPAL             │");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.println("│  1) ➕  Ajouter un animal               │");
        System.out.println("│  2) 🍖  Nourrir un animal               │");
        System.out.println("│  3) 🎾  Jouer avec un animal            │");
        System.out.println("│  4) 💊  Soigner un animal               │");
        System.out.println("│  5) 🏠  Adopter un animal               │");
        System.out.println("│  6) 📋  Afficher les animaux            │");
        System.out.println("│  7) 🗑️   Supprimer un animal            │");
        System.out.println("│  8) ⏰  Avancer d'un cycle              │");
        System.out.println("│  9) 💾  Sauvegarder                     │");
        System.out.println("│ 10) 📂  Charger depuis fichier          │");
        System.out.println("│ 11) 📊  Stats rapides (HashMap)         │");
        System.out.println("│  0) 🚪  Quitter                         │");
        System.out.println("└─────────────────────────────────────────┘");
    }

    private static void printSectionHeader(String title) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("══════════════════════════════════════");
    }


    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Veuillez entrer un nombre entier.");
            }
        }
    }


    private static int readIntPositive(String prompt) {
        while (true) {
            int val = readInt(prompt);
            if (val >= 0) return val;
            System.out.println("⚠️  La valeur doit être positive ou nulle.");
        }
    }


    private static int readIntRange(String prompt, int min, int max) {
        while (true) {
            int val = readInt(prompt);
            if (val >= min && val <= max) return val;
            System.out.println("⚠️  Entrez une valeur entre " + min + " et " + max + ".");
        }
    }


    private static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("⚠️  La saisie ne peut pas être vide.");
        }
    }


    private static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("o") || line.equals("oui") || line.equals("y") || line.equals("yes"))
                return true;
            if (line.equals("n") || line.equals("non") || line.equals("no"))
                return false;
            System.out.println("⚠️  Répondez par 'o' (oui) ou 'n' (non).");
        }
    }
}