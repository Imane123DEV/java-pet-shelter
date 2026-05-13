package shelter.model;

import shelter.interfaces.Adoptable;
import shelter.exceptions.PetRanAwayException;

public abstract class Animal implements Adoptable {
    private int id;
    private String name;
    private String species;
    private int age;
    private int health; // 0 = mort, 100 = pleine santé
    private int hunger; // 0 = rassasié, 100 = affamé
    private int mood;   // 0 = très triste, 100 = très heureux
    private boolean adopted;
    private String adopterName;

    public static final int MOOD_RUNAWAY_THRESHOLD = 20; // seuil d'humeur critique
    public static final int HUNGER_CRITICAL_THRESHOLD = 80; // seuil de faim critique

    public Animal(int id, String name, String species, int age) {
        this.id = id;
        this.name = formatName(name);
        this.species = species;
        this.age = age;
        this.health = 100;
        this.hunger = 0;
        this.mood = 80;
        this.adopted = false;
        this.adopterName = null;
    }

    public abstract void makeSound();
    public abstract void play();
    public abstract String getDescription();

    // Méthodes communes
    public void feed() {
        this.hunger = Math.max(0, this.hunger - 30);
        this.mood = Math.min(100, this.mood + 10);
        System.out.println(name + " a été nourri(e). Faim : " + hunger + ", Humeur : " + mood);
    }

    public void heal() {
        this.health = Math.min(100, this.health + 30);
        System.out.println(name + " a été soigné(e). Santé : " + health);
    }

    public void updateState() throws PetRanAwayException {
         // La faim augmente avec le temps
        this.hunger = Math.min(100, this.hunger + 15);
        // Si l'animal a trop faim, sa santé diminue
        if (this.hunger >= HUNGER_CRITICAL_THRESHOLD) {
            this.health = Math.max(0, this.health - 10);
            this.mood = Math.max(0, this.mood - 15);
        }
        // Si l'humeur est trop basse, l'animal s'enfuit
        if (this.mood <= MOOD_RUNAWAY_THRESHOLD) 
            throw new PetRanAwayException("L'animal " + name + " s'est enfui du refuge !", this);
    }

    public void displayStats() {
        System.out.println("═══════════════════════════════════");
        System.out.println("  Animal  : " + name + " (" + species + ")");
        System.out.println("  ID      : " + id);
        System.out.println("  Âge     : " + age + " ans");
        System.out.println("  Santé   : " + health + "/100");
        System.out.println("  Faim    : " + hunger + "/100");
        System.out.println("  Humeur  : " + mood + "/100");
        System.out.println("  Adopté  : " + (adopted ? "Oui, par " + adopterName : "Non"));
        System.out.println("═══════════════════════════════════");
    }

    @Override
    public void adopt(String adopterName) {
        if (this.adopted) {
            System.out.println(name + " est déjà adopté(e) par " + this.adopterName + ".");
            return;
        }
        this.adopted = true;
        this.adopterName = adopterName;
        this.mood = Math.min(100, this.mood + 20); // L'adoption améliore l'humeur 
        System.out.println(name + " a été adopté(e) par " + adopterName + " !");
    }

    @Override
    public boolean isAvailableForAdoption() {
        return !adopted && health > 30; // Doit être en bonne santé pour être adopté
    }

    private String formatName(String name) {
        if (name == null || name.isEmpty()) return "Inconnu";
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
    
    // Getters & Setters
    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = formatName(name); }

    public String getSpecies() { return species; }

    public int getAge() { return age;}
    public void setAge(int age) {
        if (age < 0) throw new IllegalArgumentException("L'âge ne peut pas être négatif.");
        this.age = age;
    }

    public int getHealth() { return health;}
    public void setHealth(int health) { this.health = Math.max(0, Math.min(100, health)); }

    public int getHunger(){ return hunger;}
    public void setHunger(int hunger) { this.hunger = Math.max(0, Math.min(100, hunger)); }

    public int getMood() { return mood;}
    public void setMood(int mood) { this.mood = Math.max(0, Math.min(100, mood));}

    public boolean isAdopted() { return adopted;}
    public void setAdopted(boolean adopted) { this.adopted = adopted; }

    public String getAdopterName() { return adopterName; }
    public void setAdopterName(String adopterName) { this.adopterName = adopterName;}

    @Override
    public String toString() {
        return String.format("Animal[id=%d, name=%s, species=%s, age=%d, health=%d, hunger=%d, mood=%d, adopted=%b]",
                id, name, species, age, health, hunger, mood, adopted);
    }
}