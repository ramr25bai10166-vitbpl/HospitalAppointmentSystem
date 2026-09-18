package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Represents a Doctor in the hospital.
// Inherits from Person and adds specialization, fee, and time-slot management.
public class Doctor extends Person {

    private String specialization;
    private double consultationFee;
    private List<String> availableSlots;

    // Constructor
    public Doctor(String id, String name, int age, String gender, String contact,
                  String specialization, double consultationFee, List<String> availableSlots) {
        super(id, name, age, gender, contact);
        this.specialization = specialization;
        this.consultationFee = consultationFee;
        this.availableSlots = new ArrayList<>(availableSlots);
    }

    // Getters and Setters

    public String getSpecialization() { return specialization; }
    public double getConsultationFee() { return consultationFee; }
    public List<String> getAvailableSlots() { return availableSlots; }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    // Returns true if the given slot is still available for this doctor.
    public boolean hasSlot(String slot) {
        return availableSlots.contains(slot);
    }

    // Removes a slot after it is booked so it cannot be double-booked.
    public void removeSlot(String slot) {
        availableSlots.remove(slot);
    }

    // Adds a slot back when an appointment is cancelled.
    public void addSlot(String slot) {
        if (!availableSlots.contains(slot)) {
            availableSlots.add(slot);
        }
    }

    // Formats doctor details for console display.
    @Override
    public String getDisplayInfo() {
        return String.format(
            "  ID             : %s%n" +
            "  Name           : %s%n" +
            "  Specialization : %s%n" +
            "  Consultation Fee: Rs. %.2f%n" +
            "  Available Slots: %s",
            getId(), getName(), specialization,
            consultationFee, availableSlots.toString()
        );
    }

    // Saves doctor to a pipe-delimited line for file storage.
    // Format: id|name|age|gender|contact|specialization|fee|slot1,slot2,...
    public String toCsvLine() {
        String slots = String.join(",", availableSlots);
        return String.join("|",
            getId(), getName(), String.valueOf(getAge()),
            getGender(), getContact(), specialization,
            String.valueOf(consultationFee), slots
        );
    }

    // Reads a pipe-delimited line and returns a Doctor object.
    public static Doctor fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        List<String> slots = new ArrayList<>();
        if (!parts[7].isBlank()) {
            slots = new ArrayList<>(Arrays.asList(parts[7].split(",")));
        }
        return new Doctor(
            parts[0], parts[1], Integer.parseInt(parts[2]),
            parts[3], parts[4], parts[5],
            Double.parseDouble(parts[6]), slots
        );
    }
}
