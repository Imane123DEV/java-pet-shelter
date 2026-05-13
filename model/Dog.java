package shelter.model;

public class Dog extends Animal {
    private String breed;
    private boolean trained;

    public Dog(int id, String name, int age, String breed) {
        super(id, name, "Chien", age);
        this.breed = breed;
        this.trained = false;
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + " : Woof! Woof!");
    }

    @Override
    public void play() {
        setMood(Math.min(100, getMood() + 25));  //jouer améliore humeur.
        setHunger(Math.min(100, getHunger() + 10)); // jouer donne faim
        System.out.println(getName() + " rapporte la balle ! Humeur : " + getMood());
    }

    @Override
    public String getDescription() {
        return String.format("%s est un chien de race %s, âgé de %d ans. %s",
            getName(), breed, getAge(), trained ? "Il est bien dressé." : "Il n'est pas encore dressé.");
    }

    public void train() {
        this.trained = true;
        setMood(Math.min(100, getMood() + 15));
        System.out.println(getName() + " a été dressé!");
    }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed;}
    public boolean isTrained() {return trained;}
    public void setTrained(boolean trained) { this.trained = trained; }
}