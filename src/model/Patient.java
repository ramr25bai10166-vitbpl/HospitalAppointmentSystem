package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Represents a Patient in the hospital.
// Inherits from Person and adds medical-history specific fields.
public class Patient extends Person {

    private String medicalHistory;

    // Constructor
    public Patient(String id, String name, int age, String gender,
                   String contact, String medicalHistory) {
        super(id, name, age, gender, contact);
        this.medicalHistory = medicalHistory;
    }

    // Getter and Setter

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    // Formats patient details for console display.
    @Override
    public String getDisplayInfo() {
        return String.format(
            "  ID      : %s%n" +
            "  Name    : %s%n" +
            "  Age     : %d%n" +
            "  Gender  : %s%n" +
            "  Contact : %s%n" +
            "  History : %s",
            getId(), getName(), getAge(), getGender(), getContact(), medicalHistory
        );
    }

    // Saves patient to a pipe-delimited line for file storage.
    // Format: id|name|age|gender|contact|medicalHistory
    public String toCsvLine() {
        return String.join("|",
            getId(), getName(), String.valueOf(getAge()),
            getGender(), getContact(), medicalHistory
        );
    }

    // Reads a pipe-delimited line and returns a Patient object.
    public static Patient fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        return new Patient(parts[0], parts[1], Integer.parseInt(parts[2]),
                           parts[3], parts[4], parts[5]);
    }
}
