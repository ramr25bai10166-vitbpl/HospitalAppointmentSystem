package model;

// Abstract base class representing a Person in the hospital system.
// Both Patient and Doctor inherit from this class (OOP Inheritance).
// Demonstrates encapsulation via private fields and public getters/setters.
public abstract class Person {

    private String id; // Unique ID (e.g., P001 for patients, D001 for doctors)
    private String name;
    private int age;
    private String gender;
    private String contact;

    // Constructor
    public Person(String id, String name, int age, String gender, String contact) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
    }

    // Getters

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getContact() { return contact; }

    // Setters

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setContact(String contact) { this.contact = contact; }

    // Abstract method - every subclass must provide its own display string.
    public abstract String getDisplayInfo();
}
