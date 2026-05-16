# 🐾 Happy Paws Shelter

A Java console application for managing a virtual animal shelter — built as a university OOP project. Adopt pets, feed them, play with them, and make sure they don't run away!

---

## 📁 Project Structure

```
shelter/
├── exceptions/
│   ├── InvalidPetOperationException.java   # Custom exception for invalid operations
│   └── PetRanAwayException.java            # Thrown when a neglected animal escapes
├── interfaces/
│   └── Adoptable.java                      # Interface implemented by all animals
├── model/
│   ├── Animal.java                         # Abstract base class
│   ├── Dog.java                            # Subclass with breed & training
│   ├── Cat.java                            # Subclass with independence level
│   └── Parrot.java                         # Subclass that learns words
├── service/
│   ├── ShelterManager.java                 # CRUD operations & business logic
│   └── ShelterFileManager.java             # File I/O (CSV + Properties)
└── Main.java                               # Console menu & entry point
```

---

## ✨ Features

| Feature | Description |
|---|---|
| ➕ Add animals | Create Dogs, Cats, or Parrots with custom attributes |
| 🍖 Feed | Reduces hunger, improves mood |
| 🎾 Play | Boosts mood, triggers animal sounds |
| 💊 Heal | Restores health |
| 🏠 Adopt | Assign an animal to an adopter (with eligibility checks) |
| 📋 Display | List all, view details, search by name, or filter adoptable |
| 🗑️ Remove | Delete an animal from the shelter (with confirmation) |
| ⏰ Time cycle | Advances time — hunger rises, mood drops, animals may escape! |
| 💾 Save | Persist data to CSV and `.properties` file |
| 📂 Load | Restore animals from a saved CSV file |
| 📊 Stats | Quick HashMap overview of all animal stats |

### Species-specific actions
- 🐕 **Dog** — can be trained (`train()`)
- 🐈 **Cat** — can be groomed or made to purr (`groom()`, `purr()`)
- 🦜 **Parrot** — can learn and repeat words (`learnWord()`, `makeSound()`)

---

## 🧱 OOP Concepts Used

- **Abstraction** — `Animal` is an abstract class with abstract methods `makeSound()`, `play()`, `getDescription()`
- **Inheritance** — `Dog`, `Cat`, `Parrot` extend `Animal`
- **Polymorphism** — animals are stored as `Animal` but behave according to their real type
- **Interfaces** — `Adoptable` interface implemented by all animals
- **Encapsulation** — all attributes are private with getters/setters
- **Custom Exceptions** — `InvalidPetOperationException`, `PetRanAwayException`
- **Collections** — `ArrayList` and `HashMap` used in `ShelterManager`
- **File I/O** — CSV read/write with `BufferedWriter`, `Scanner`, `FileReader`
- **String manipulation** — name formatting, CSV parsing, display formatting

---

## 🚀 Getting Started

### Requirements
- Java 17 or higher
- Any IDE (IntelliJ IDEA recommended) or command line

### Run in IntelliJ
1. Clone the repository
2. Open the project in IntelliJ
3. Make sure `shelter` is marked as the sources root
4. Run `Main.java`

### Run from command line
```bash
# Compile
javac -d out shelter/**/*.java shelter/Main.java

# Run
java -cp out shelter.Main
```

### First launch
No setup needed. On first run, 5 example animals are loaded automatically:

| ID | Name | Species |
|----|------|---------|
| 1 | Rex | Dog (German Shepherd, trained) |
| 2 | Minou | Cat (long hair, affectionate) |
| 3 | Coco | Parrot (knows "bonjour" & "biscuit") |
| 4 | Luna | Dog (Labrador puppy) |
| 5 | Felix | Cat (independent) |

Data is saved automatically to `shelter_data.csv` when you quit.

---

## 💾 Data Persistence

Animals are saved in two formats:

**`shelter_data.csv`** — main storage, loaded on every startup
```
id;name;species;age;health;hunger;mood;adopted;adopterName;extra1;extra2
1;Rex;Chien;3;100;0;80;false;;Berger Allemand;true
```

**`shelter_stats.properties`** — key-value snapshot of all stats (bonus format)

---

## ⚠️ Exception Handling

| Exception | When it's thrown |
|---|---|
| `InvalidPetOperationException` | Duplicate ID, animal not found, invalid adopter name, unhealthy animal adoption |
| `PetRanAwayException` | Animal's mood drops to 20 or below after a time cycle |
| `IOException` | File not found or unreadable during load/save |
| `IllegalArgumentException` | Negative age or out-of-range stat values |

All exceptions are caught gracefully — the app never crashes abruptly.

---

## 👥 Team

| Member | Responsibility |
|---|---|
| Malak Bitane | Animal entities (`Animal`, `Dog`, `Cat`, `Parrot`, `Adoptable`) |
| Siham Jdira | CRUD operations (`ShelterManager`) |
| Fatima-Zahra El magana | File persistence (`ShelterFileManager`) |
| Imane Ait-mouh | Custom exceptions |
| Khawla El hamdi | Console interface & integration (`Main.java`) |

---

