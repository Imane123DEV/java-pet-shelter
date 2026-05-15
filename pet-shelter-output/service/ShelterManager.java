package shelter.service;

import shelter.model.Animal;
import shelter.exceptions.PetRanAwayException;
import shelter.exceptions.InvalidPetOperationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShelterManager {

    // Liste de tous les animaux du refuge
    private final List<Animal> animals;

    // Table de correspondance ID : Animal (pour retrouver un animal rapidement)
    private final Map<Integer, Animal> animalById;

    public ShelterManager() {
        this.animals    = new ArrayList<>();
        this.animalById = new HashMap<>();
    }

    // ── Ajouter un animal ────────────────────────────────────────────────────

    // Ajoute un animal dans le refuge si son ID n'existe pas déjà
    public void addPet(Animal animal) throws InvalidPetOperationException {
        if (animal == null) {
            throw new InvalidPetOperationException(
                "Impossible d'ajouter un animal null.", "NULL_ANIMAL");
        }
        if (animalById.containsKey(animal.getId())) {
            throw new InvalidPetOperationException(
                "Un animal avec l'ID " + animal.getId() + " existe déjà dans le refuge.",
                "DUPLICATE_ID");
        }
        animals.add(animal);
        animalById.put(animal.getId(), animal);
        System.out.println(animal.getName() + " (" + animal.getSpecies()
            + ") ajouté(e) au refuge. [ID=" + animal.getId() + "]");
    }

    // ── Afficher les animaux ─────────────────────────────────────────────────

    // Affiche tous les animaux du refuge
    public void listPets() {
        if (animals.isEmpty()) {
            System.out.println("Le refuge est vide.");
            return;
        }
        System.out.println("\n--- Liste des animaux ---");
        for (Animal a : animals) {
            String adoption = a.isAdopted()
                ? "adopté par " + a.getAdopterName()
                : "non adopté";
            System.out.println("[" + a.getId() + "] " + a.getName()
                + " | " + a.getSpecies()
                + " | Santé: " + a.getHealth()
                + " | Faim: "  + a.getHunger()
                + " | Humeur: " + a.getMood()
                + " | " + adoption);
        }
        System.out.println("Total : " + animals.size() + " animal(aux)\n");
    }

    // Affiche les détails complets d'un animal selon son ID
    public void displayPetStats(int id) {
        Animal animal = findById(id);
        if (animal == null) {
            System.out.println("Aucun animal trouvé avec l'ID " + id);
            return;
        }
        animal.displayStats();
        System.out.println("  Description : " + animal.getDescription());
    }

    // Affiche uniquement les animaux qui peuvent être adoptés
    public void listAdoptableAnimals() {
        List<Animal> adoptable = getAdoptableAnimals();
        if (adoptable.isEmpty()) {
            System.out.println("Aucun animal disponible à l'adoption pour le moment.");
            return;
        }
        System.out.println("\n--- Animaux disponibles à l'adoption ---");
        for (Animal a : adoptable) {
            System.out.println("[" + a.getId() + "] " + a.getName()
                + " — " + a.getDescription());
        }
        System.out.println();
    }

    // ── Rechercher un animal ─────────────────────────────────────────────────

    // Retourne l'animal correspondant à l'ID donné, ou null s'il n'existe pas
    public Animal findById(int id) {
        return animalById.get(id);
    }

    // Retourne la liste des animaux dont le nom contient le texte recherché
    public List<Animal> findByName(String namePart) {
        List<Animal> result = new ArrayList<>();
        if (namePart == null || namePart.isBlank()) return new ArrayList<>(animals);
        String query = namePart.trim().toLowerCase();
        for (Animal a : animals) {
            if (a.getName().toLowerCase().contains(query)) {
                result.add(a);
            }
        }
        return result;
    }

    // Retourne la liste des animaux disponibles à l'adoption
    public List<Animal> getAdoptableAnimals() {
        List<Animal> result = new ArrayList<>();
        for (Animal a : animals) {
            if (a.isAvailableForAdoption()) result.add(a);
        }
        return result;
    }

    // ── Modifier les stats d'un animal ───────────────────────────────────────

    /**
     * Nourrit un animal (réduit sa faim et améliore son humeur).
     * try/catch/finally : on log toujours l'opération dans le finally,
     * et on lance InvalidPetOperationException si l'animal est introuvable.
     */
    public void feedPet(int id) throws InvalidPetOperationException {
        Animal animal = null;
        try {
            animal = findById(id);
            if (animal == null) {
                throw new InvalidPetOperationException(
                    "Impossible de nourrir : aucun animal avec l'ID " + id + ".",
                    "ANIMAL_NOT_FOUND");
            }
            animal.feed();
        } catch (InvalidPetOperationException e) {
            System.err.println("[Erreur] " + e);
            throw e; // on relaie pour que l'appelant puisse réagir
        } finally {
            // Le finally s'exécute toujours, succès ou échec
            System.out.println("[Log] Tentative de nourrissage — ID=" + id
                + (animal != null ? " (" + animal.getName() + ")" : " (animal introuvable)"));
        }
    }

    /**
     * Soigne un animal (augmente sa santé).
     * try/catch/finally + throws InvalidPetOperationException.
     */
    public void healPet(int id) throws InvalidPetOperationException {
        Animal animal = null;
        try {
            animal = findById(id);
            if (animal == null) {
                throw new InvalidPetOperationException(
                    "Impossible de soigner : aucun animal avec l'ID " + id + ".",
                    "ANIMAL_NOT_FOUND");
            }
            animal.heal();
        } catch (InvalidPetOperationException e) {
            System.err.println("[Erreur] " + e);
            throw e;
        } finally {
            System.out.println("[Log] Tentative de soin — ID=" + id
                + (animal != null ? " (" + animal.getName() + ")" : " (animal introuvable)"));
        }
    }

    // Modifie directement la valeur de santé d'un animal
    public void updateHealth(int id, int newHealth) {
        Animal animal = findById(id);
        if (animal == null) {
            System.out.println("Animal ID=" + id + " introuvable.");
            return;
        }
        int before = animal.getHealth();
        animal.setHealth(newHealth);
        System.out.println("Santé de " + animal.getName() + " : " + before + " → " + animal.getHealth());
    }

    // Modifie directement la valeur de faim d'un animal
    public void updateHunger(int id, int newHunger) {
        Animal animal = findById(id);
        if (animal == null) {
            System.out.println("Animal ID=" + id + " introuvable.");
            return;
        }
        int before = animal.getHunger();
        animal.setHunger(newHunger);
        System.out.println("Faim de " + animal.getName() + " : " + before + " → " + animal.getHunger());
    }

    // Modifie directement la valeur d'humeur d'un animal
    public void updateMood(int id, int newMood) {
        Animal animal = findById(id);
        if (animal == null) {
            System.out.println("Animal ID=" + id + " introuvable.");
            return;
        }
        int before = animal.getMood();
        animal.setMood(newMood);
        System.out.println("Humeur de " + animal.getName() + " : " + before + " → " + animal.getMood());
    }

    /**
     * Fait jouer un animal (améliore son humeur).
     * try/catch/finally + throws InvalidPetOperationException.
     */
    public void playWithPet(int id) throws InvalidPetOperationException {
        Animal animal = null;
        try {
            animal = findById(id);
            if (animal == null) {
                throw new InvalidPetOperationException(
                    "Impossible de jouer : aucun animal avec l'ID " + id + ".",
                    "ANIMAL_NOT_FOUND");
            }
            animal.play();
        } catch (InvalidPetOperationException e) {
            System.err.println("[Erreur] " + e);
            throw e;
        } finally {
            System.out.println("[Log] Tentative de jeu — ID=" + id
                + (animal != null ? " (" + animal.getName() + ")" : " (animal introuvable)"));
        }
    }

    /**
     * Avance d'un cycle de temps : met à jour l'état de chaque animal.
     * Si un animal s'enfuit (humeur trop basse), il est retiré du refuge.
     * try/catch conservé, finally ajouté pour le résumé du cycle.
     */
    public void updateAllStates() {
        System.out.println("Avance d'un cycle — mise à jour de tous les animaux...");
        List<Animal> toRemove = new ArrayList<>();
        int runawayCount = 0;

        try {
            for (Animal a : animals) {
                try {
                    a.updateState();
                } catch (PetRanAwayException e) {
                    System.out.println("! " + e.getMessage());
                    toRemove.add(e.getAnimal()); //on utilise getAnimal() pour être précis
                    runawayCount++;
                }
            }

            for (Animal a : toRemove) {
                removeFromCollections(a);
                System.out.println(a.getName() + " [ID=" + a.getId() + "] retiré(e) du refuge.");
            }

        } finally {
            // le finally garantit que le résumé s'affiche même en cas d'erreur inattendue
            System.out.println("Cycle terminé. Animaux restants : " + animals.size()
                + " | Fugues ce cycle : " + runawayCount + "\n");
        }
    }

    /**
     * Adopte un animal au nom d'une personne (vérifie d'abord qu'il est disponible).
     * try/catch/finally ajouté autour de l'opération d'adoption.
     */
    public void adoptPet(int id, String adopterName) throws InvalidPetOperationException {
        Animal animal = null;
        try {
            if (adopterName == null || adopterName.isBlank()) {
                //validation du nom de l'adoptant
                throw new InvalidPetOperationException(
                    "Le nom de l'adoptant ne peut pas être vide.", "INVALID_ADOPTER_NAME");
            }
            animal = findById(id);
            if (animal == null) {
                throw new InvalidPetOperationException(
                    "Animal ID=" + id + " introuvable.", "ANIMAL_NOT_FOUND");
            }
            if (!animal.isAvailableForAdoption()) {
                throw new InvalidPetOperationException(
                    animal.getName() + " n'est pas disponible à l'adoption "
                    + "(déjà adopté ou santé insuffisante).", "NOT_ADOPTABLE");
            }
            animal.adopt(adopterName);

        } catch (InvalidPetOperationException e) {
            System.err.println("[Erreur adoption] " + e);
            throw e;
        } finally {
            System.out.println("[Log] Tentative d'adoption — ID=" + id
                + ", adoptant=" + adopterName
                + (animal != null ? " (" + animal.getName() + ")" : " (animal introuvable)"));
        }
    }

    // ── Supprimer un animal ──────────────────────────────────────────────────

    /**
     * Supprime un animal du refuge par son ID.
     * Retourne true si la suppression a réussi, false sinon.
     * finally ajouté pour le log.
     */
    public boolean removePet(int id) throws InvalidPetOperationException {
        Animal animal = null;
        try {
            animal = findById(id);
            if (animal == null) {
                throw new InvalidPetOperationException(
                    "Aucun animal avec l'ID " + id + " dans le refuge.", "ANIMAL_NOT_FOUND");
            }
            removeFromCollections(animal);
            System.out.println(animal.getName() + " [ID=" + id + "] retiré(e) du refuge.");
            return true;

        } catch (InvalidPetOperationException e) {
            System.err.println("[Erreur suppression] " + e);
            throw e;
        } finally {
            System.out.println("[Log] Tentative de suppression — ID=" + id
                + (animal != null ? " (" + animal.getName() + ")" : " (animal introuvable)"));
        }
    }

    // ── Méthodes privées ─────────────────────────────────────────────────────

    // Retire un animal des deux collections en même temps
    private void removeFromCollections(Animal animal) {
        animals.remove(animal);
        animalById.remove(animal.getId());
    }
}
