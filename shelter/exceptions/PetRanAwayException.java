package shelter.exceptions;

import shelter.model.Animal;

public class PetRanAwayException extends Exception {
    private final Animal animal; // l'animal qui s'est enfui

    public PetRanAwayException(String message, Animal animal) {
        super(message);
        this.animal = animal;
    }

    public PetRanAwayException(String message, Animal animal, Throwable cause) {
        super(message, cause);
        this.animal = animal;
    }

    public Animal getAnimal() {
        return animal;
    }
}