package shelter.service;

import shelter.model.Animal;
import shelter.model.Dog;
import shelter.model.Cat;
import shelter.model.Parrot;
import shelter.exceptions.InvalidPetOperationException;

import java.io.*;
import java.util.*;

public class ShelterFileManager {

    // Nom du fichier CSV utilisé
    private static final String DEFAULT_FILE_PATH = "shelter_data.csv";

    // Caractère qui sépare les colonnes dans le CSV (point-virgule)
    private static final String SEPARATOR = ";";

    /*
     * extra1 et extra2 contiennent des infos propres à chaque espèce :
     *   - Chien     : extra1 = race           | extra2 = dressé (true/false)
     *   - Chat      : extra1 = poilsLongs     | extra2 = niveauIndépendance
     *   - Perroquet : extra1 = couleurPlumage  | extra2 = peutVoler (true/false)
     */
    private static final String CSV_HEADER =
        "id;name;species;age;health;hunger;mood;adopted;adopterName;extra1;extra2";

    // ── Sauvegarde ───────────────────────────────────────────────────────────

    // Sauvegarde tous les animaux dans le fichier CSV par défaut.
    public void saveToFile(List<Animal> animals) throws IOException {
        saveToFile(animals, DEFAULT_FILE_PATH);
    }

    /**
     * Sauvegarde tous les animaux dans le fichier CSV dont le chemin est donné.
     */
    public void saveToFile(List<Animal> animals, String filePath) throws IOException {
        // validation en amont avec InvalidPetOperationException (non checked ici,
        // mais on évite un crash silencieux si la liste est null)
        if (animals == null) {
            System.err.println("[Erreur] La liste d'animaux est null — sauvegarde annulée.");
            return;
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath, false));
            writer.write(CSV_HEADER);
            writer.newLine();

            for (Animal a : animals) {
                writer.write(buildCsvLine(a));
                writer.newLine();
            }

            System.out.println("[Sauvegarde] " + animals.size()
                + " animal(aux) sauvegardé(s) dans « " + filePath + " ».");

        } catch (IOException e) {
            System.err.println("[Erreur] Impossible de sauvegarder : " + e.getMessage());
            throw e;   // on relaie pour que l'appelant puisse réagir

        } finally {
            // fermeture manuelle pour montrer le finally explicitement
            // (le try-with-resources ferme automatiquement, mais ici on démontre finally)
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException closeEx) {
                    System.err.println("[Avertissement] Problème à la fermeture du fichier : "
                        + closeEx.getMessage());
                }
            }
            System.out.println("[Log] Opération saveToFile terminée pour : " + filePath);
        }
    }

    // ── Chargement ───────────────────────────────────────────────────────────

    // Charge les animaux depuis le fichier CSV par défaut.
    public List<Animal> loadFromFile() throws IOException {
        return loadFromFile(DEFAULT_FILE_PATH);
    }

    /**
     * Charge les animaux depuis le fichier CSV dont le chemin est donné.
     * finally pour garantir l'affichage du bilan même en cas d'erreur.
     */
    public List<Animal> loadFromFile(String filePath) throws IOException {
        List<Animal> animals = new ArrayList<>();
        File file = new File(filePath);
        int lineNumber = 0;
        int skipped    = 0;

        // Vérification préalable
        if (!file.exists()) {
            throw new FileNotFoundException(
                "[Erreur] Fichier introuvable : « " + filePath + " »");
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileReader(filePath));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;
                if (lineNumber == 1 || line.isEmpty()) continue;

                try {
                    Animal animal = parseCsvLine(line);
                    if (animal != null) animals.add(animal);
                } catch (Exception e) {
                    System.err.println("[Avertissement] Ligne " + lineNumber
                        + " ignorée : " + e.getMessage());
                    skipped++;
                }
            }

            System.out.println("[Chargement] " + animals.size()
                + " animal(aux) chargé(s) depuis « " + filePath + " ».");

        } catch (FileNotFoundException e) {
            System.err.println("[Erreur] Fichier introuvable : " + filePath);
            throw e;
        } catch (IOException e) {
            System.err.println("[Erreur] Lecture impossible : " + e.getMessage());
            throw e;

        } finally {
            //fermeture du scanner et bilan systématique
            if (scanner != null) scanner.close();
            System.out.println("[Log] loadFromFile terminé — lignes lues : " + lineNumber
                + " | ignorées : " + skipped + " | chargées : " + animals.size());
        }

        return animals;
    }

    // ── Sauvegarde Properties ────────────────────────────────────────────────

    public void saveToProperties(List<Animal> animals, String propsFilePath) throws IOException {
        Properties props = new Properties();

        for (Animal a : animals) {
            String key = a.getName().toLowerCase().replace(" ", "_") + "_" + a.getId();
            props.setProperty(key + ".id",      String.valueOf(a.getId()));
            props.setProperty(key + ".name",    a.getName());
            props.setProperty(key + ".species", a.getSpecies());
            props.setProperty(key + ".age",     String.valueOf(a.getAge()));
            props.setProperty(key + ".health",  String.valueOf(a.getHealth()));
            props.setProperty(key + ".hunger",  String.valueOf(a.getHunger()));
            props.setProperty(key + ".mood",    String.valueOf(a.getMood()));
            props.setProperty(key + ".adopted", String.valueOf(a.isAdopted()));
            props.setProperty(key + ".adopter",
                a.getAdopterName() == null ? "" : a.getAdopterName());
        }

        // try/catch/finally explicite autour de l'écriture
        FileWriter fw = null;
        try {
            fw = new FileWriter(propsFilePath);
            props.store(fw, "Shelter – stats des animaux");
            System.out.println("[Properties] Sauvegardé dans « " + propsFilePath + " ».");
        } catch (IOException e) {
            System.err.println("[Erreur] Impossible d'écrire le fichier properties : "
                + e.getMessage());
            throw e;
        } finally {
            if (fw != null) {
                try { fw.close(); }
                catch (IOException closeEx) {
                    System.err.println("[Avertissement] Fermeture properties échouée : "
                        + closeEx.getMessage());
                }
            }
            System.out.println("[Log] saveToProperties terminé pour : " + propsFilePath);
        }
    }

    // ── Stats HashMap ────────────────────────────────────────────────────────

    public HashMap<String, String> buildStatsMap(List<Animal> animals) {
        HashMap<String, String> statsMap = new HashMap<>();
        for (Animal a : animals) {
            String stats = String.format(
                "Santé=%d | Faim=%d | Humeur=%d | Adopté=%s",
                a.getHealth(), a.getHunger(), a.getMood(),
                a.isAdopted() ? "Oui (" + a.getAdopterName() + ")" : "Non"
            );
            statsMap.put(a.getName() + " [ID=" + a.getId() + "]", stats);
        }
        return statsMap;
    }

    public void displayStatsMap(List<Animal> animals) {
        HashMap<String, String> map = buildStatsMap(animals);
        System.out.println("\n--- Stats rapides (HashMap) ---");
        map.forEach((name, stats) -> System.out.println("  " + name + " → " + stats));
        System.out.println();
    }

    // ── Méthodes privées ─────────────────────────────────────────────────────

    // Convertit un objet Animal en une ligne de texte CSV.
    private String buildCsvLine(Animal a) {
        String extra1 = "";
        String extra2 = "";

        if (a instanceof Dog) {
            Dog d = (Dog) a;
            extra1 = d.getBreed();
            extra2 = String.valueOf(d.isTrained());
        } else if (a instanceof Cat) {
            Cat c = (Cat) a;
            extra1 = String.valueOf(c.isLongHair());
            extra2 = String.valueOf(c.getIndependenceLevel());
        } else if (a instanceof Parrot) {
            Parrot p = (Parrot) a;
            extra1 = p.getPlumageColor();
            extra2 = String.valueOf(p.canFly());
        }

        return String.join(SEPARATOR,
            String.valueOf(a.getId()),
            a.getName(),
            a.getSpecies(),
            String.valueOf(a.getAge()),
            String.valueOf(a.getHealth()),
            String.valueOf(a.getHunger()),
            String.valueOf(a.getMood()),
            String.valueOf(a.isAdopted()),
            a.getAdopterName() == null ? "" : a.getAdopterName(),
            extra1,
            extra2
        );
    }

    // Reconstruit un objet Animal depuis une ligne CSV.
    private Animal parseCsvLine(String line) {
        String[] parts = line.split(SEPARATOR, -1);

        if (parts.length < 11) {
            throw new IllegalArgumentException(
                "Nombre de colonnes insuffisant (" + parts.length + "/11)");
        }

        int     id          = Integer.parseInt(parts[0].trim());
        String  name        = parts[1].trim();
        String  species     = parts[2].trim();
        int     age         = Integer.parseInt(parts[3].trim());
        int     health      = Integer.parseInt(parts[4].trim());
        int     hunger      = Integer.parseInt(parts[5].trim());
        int     mood        = Integer.parseInt(parts[6].trim());
        boolean adopted     = Boolean.parseBoolean(parts[7].trim());
        String  adopterName = parts[8].trim().isEmpty() ? null : parts[8].trim();
        String  extra1      = parts[9].trim();
        String  extra2      = parts[10].trim();

        Animal animal;

        switch (species.toLowerCase()) {
            case "chien":
                String  breed   = extra1.isEmpty() ? "Inconnue" : extra1;
                boolean trained = Boolean.parseBoolean(extra2);
                Dog dog = new Dog(id, name, age, breed);
                dog.setTrained(trained);
                animal = dog;
                break;

            case "chat":
                boolean longHair          = Boolean.parseBoolean(extra1);
                int     independenceLevel = extra2.isEmpty() ? 50 : Integer.parseInt(extra2);
                animal = new Cat(id, name, age, longHair, independenceLevel);
                break;

            case "perroquet":
                String  plumageColor = extra1.isEmpty() ? "Inconnu" : extra1;
                boolean canFly       = Boolean.parseBoolean(extra2);
                animal = new Parrot(id, name, age, plumageColor, canFly);
                break;

            default:
                System.err.println("[Avertissement] Espèce inconnue « " + species + " » — ignoré.");
                return null;
        }

        animal.setHealth(health);
        animal.setHunger(hunger);
        animal.setMood(mood);
        animal.setAdopted(adopted);
        animal.setAdopterName(adopterName);

        return animal;
    }
}
