package storage;

import model.Appointment;
import model.Doctor;
import model.Patient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Handles all file I/O for persisting and loading application data.
// Uses simple pipe-delimited (|) plain-text files.
// Files are stored in a "data/" directory relative to the working directory.
public class FileHandler {

    // Paths for each data file
    private static final String DATA_DIR = "data/";
    private static final String PATIENTS_FILE = DATA_DIR + "patients.txt";
    private static final String DOCTORS_FILE = DATA_DIR + "doctors.txt";
    private static final String APPTS_FILE = DATA_DIR + "appointments.txt";

    // Ensures the data directory exists before any file operations.
    public static void initDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Patient persistence

    // Saves the full list of patients to patients.txt (overwrites the file).
    public static void savePatients(List<Patient> patients) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATIENTS_FILE))) {
            for (Patient p : patients) {
                writer.write(p.toCsvLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error: Could not save patients: " + e.getMessage());
        }
    }

    // Loads all patients from patients.txt.
    // Returns an empty list if the file does not yet exist.
    public static List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) return patients;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    patients.add(Patient.fromCsvLine(line.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Could not load patients: " + e.getMessage());
        }
        return patients;
    }

    // Doctor persistence

    // Saves the full list of doctors to doctors.txt (overwrites the file).
    public static void saveDoctors(List<Doctor> doctors) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOCTORS_FILE))) {
            for (Doctor d : doctors) {
                writer.write(d.toCsvLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error: Could not save doctors: " + e.getMessage());
        }
    }

    // Loads all doctors from doctors.txt.
    // Returns an empty list if the file does not yet exist.
    public static List<Doctor> loadDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        File file = new File(DOCTORS_FILE);
        if (!file.exists()) return doctors;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    doctors.add(Doctor.fromCsvLine(line.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Could not load doctors: " + e.getMessage());
        }
        return doctors;
    }

    // Appointment persistence

    // Saves the full list of appointments to appointments.txt (overwrites the file).
    public static void saveAppointments(List<Appointment> appointments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPTS_FILE))) {
            for (Appointment a : appointments) {
                writer.write(a.toCsvLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error: Could not save appointments: " + e.getMessage());
        }
    }

    // Loads all appointments from appointments.txt.
    // Returns an empty list if the file does not yet exist.
    public static List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        File file = new File(APPTS_FILE);
        if (!file.exists()) return appointments;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    appointments.add(Appointment.fromCsvLine(line.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Could not load appointments: " + e.getMessage());
        }
        return appointments;
    }
}
