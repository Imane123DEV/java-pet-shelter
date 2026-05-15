package shelter.model;

import java.util.ArrayList;
import java.util.List;

public class Parrot extends Animal {
    private String plumageColor;
    private List<String> learnedWords; // mots appris par le perroquet
    private boolean canFly; //Indique si le perroquet peut voler (certains ont les ailes rognées)

    public Parrot(int id, String name, int age, String plumageColor, boolean canFly) {
        super(id, name, "Perroquet", age);
        this.plumageColor = plumageColor;
        this.canFly  = canFly;
        this.learnedWords = new ArrayList<>();
    }

    @Override
    // Répète un mot aléatoire parmi ceux appris
    public void makeSound() {
        if (learnedWords.isEmpty()) {
            System.out.println(getName() + " : Squawk !");
        } else {  
            String word = learnedWords.get((int)(Math.random() * learnedWords.size()));
            System.out.println(getName() + " : « " + word.toUpperCase() + " ! »");
        }
    }

    @Override
     //Le perroquet joue en voltigeant (s'il peut voler) ou en se balançant.
    public void play() {
        setMood(Math.min(100, getMood() + 20));
        if (canFly) {
            System.out.println(getName() + " voltige dans la pièce! Humeur : " + getMood());
        } else {
            System.out.println(getName() + " se balance sur son perchoir! Humeur : " + getMood());
        }
    }

    @Override
    public String getDescription() {
        return String.format("%s est un perroquet au plumage %s, âgé de %d ans. Il connaît %d mot(s). %s",
            getName(), plumageColor, getAge(), learnedWords.size(),
            canFly ? "Il peut voler." : "Ses ailes sont rognées.");
    }

    public void learnWord(String word) {
        if (word == null || word.trim().isEmpty()) return;
        String cleaned = word.trim().toLowerCase(); //Traitement de String
        if (!learnedWords.contains(cleaned)) {
            learnedWords.add(cleaned);
            setMood(Math.min(100, getMood() + 10));
            System.out.println(getName() + " a appris le mot « " + cleaned + " » !");
        } else {
            System.out.println(getName() + " connaît déjà ce mot.");
        }
    }

    public String getLearnedWordsAsString() {
        if (learnedWords.isEmpty()) return "(aucun mot appris)";
        return String.join(", ", learnedWords);
    }

    public String getPlumageColor() { return plumageColor; }
    public void setPlumageColor(String color) { this.plumageColor = color; }

    public boolean canFly()  { return canFly; }
    public void setCanFly(boolean canFly) { this.canFly = canFly; }

    public List<String> getLearnedWords() { return new ArrayList<>(learnedWords); }
}
