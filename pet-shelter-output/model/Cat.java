package shelter.model;

public class Cat extends Animal {
    private boolean longHair;
    private int independenceLevel; // 0 = très câlin, 100 = très indépendant

    public Cat(int id, String name, int age, boolean longHair, int independenceLevel) {
        super(id, name, "Chat", age);
        this.longHair = longHair;
        this.independenceLevel = Math.max(0, Math.min(100, independenceLevel));
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + " : Miaou~");
    }

    @Override
    public void play() {
        int moodBoost = independenceLevel > 60 ? 8 : 18; // les chats indépendants jouent moins
        setMood(Math.min(100, getMood() + moodBoost));
        System.out.println(getName() + " joue avec la pelote. Humeur : " + getMood());
    }

    @Override
    public String getDescription() {
        String hair = longHair ? "poils longs" : "poils courts";
        String personality = independenceLevel > 60 ? "très indépendant(e)" : "câlin(e)";
        return String.format("%s est un chat à %s, âgé de %d ans. Il/Elle est %s.",
            getName(), hair, getAge(), personality);
    }

    //Le chat ronronne : améliore son humeur et réduit légèrement la faim.
    public void purr() {
        setMood(Math.min(100, getMood() + 12));
        System.out.println(getName() + " ronronne... Humeur : " + getMood());
    }

    //Brosse le chat (si poils longs, améliore encore plus l'humeur).
    public void groom() {
        int boost = longHair ? 20 : 10; 
        setMood(Math.min(100, getMood() + boost));
        System.out.println(getName() + " a été brossé(e).");
    }

    public boolean isLongHair() { return longHair; }
    public void setLongHair(boolean longHair) { this.longHair = longHair; }
    public int getIndependenceLevel() { return independenceLevel; }
    public void setIndependenceLevel(int level)  { this.independenceLevel = Math.max(0, Math.min(100, level)); }
}