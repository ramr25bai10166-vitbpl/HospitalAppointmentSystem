import exceptions.InvalidAppointmentException;
import exceptions.PatientNotFoundException;
import exceptions.SlotUnavailableException;
import model.Appointment;
import model.Doctor;
import model.Patient;
import service.HospitalService;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// Hospital Appointment System
// Runs a numbered menu loop using Scanner for all user interaction.
// All business logic is delegated to HospitalService.
public class Main {

    private static final HospitalService service = new HospitalService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1  -> registerPatient();
                case 2  -> viewSearchPatients();
                case 3  -> updatePatient();
                case 4  -> addDoctor();
                case 5  -> viewDoctors();
                case 6  -> bookAppointment();
                case 7  -> cancelAppointment();
                case 8  -> viewAppointments();
                case 9  -> completeAppointment();
                case 10 -> { System.out.println("\n  Goodbye! Stay Healthy.\n"); running = false; }
                default -> System.out.println("  Error: Invalid option. Please enter 1-10.");
            }
        }
        scanner.close();
    }

    // MENU HANDLERS

    // Option 1 - Register a new patient
    private static void registerPatient() {
        printSection("REGISTER NEW PATIENT");
        try {
            String name = readString("Full Name         : ");
            int age = readInt("Age               : ");
            String gender = readString("Gender (M/F/Other): ");
            String cont = readString("Contact (Phone)   : ");
            String hist = readString("Medical History   : ");

            Patient p = service.registerPatient(name, age, gender, cont, hist);
            System.out.println("\n  [OK] Patient registered successfully!");
            System.out.println(p.getDisplayInfo());
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // Option 2 - View all patients or search by name/ID
    private static void viewSearchPatients() {
        printSection("VIEW / SEARCH PATIENTS");
        System.out.print("  Search (name/ID) or press Enter to list all: ");
        String query = scanner.nextLine().trim();

        List<Patient> results = query.isEmpty()
            ? service.getAllPatients()
            : service.searchPatients(query);

        if (results.isEmpty()) {
            System.out.println("  No patients found.");
        } else {
            System.out.println("\n  Found " + results.size() + " patient(s):\n");
            results.forEach(p -> {
                System.out.println(p.getDisplayInfo());
                System.out.println();
            });
        }
    }

    // Option 3 - Update an existing patient's details
    private static void updatePatient() {
        printSection("UPDATE PATIENT DETAILS");
        try {
            String id = readString("Enter Patient ID (e.g. P001): ");
            // Show current details first
            Patient existing = service.findPatientById(id);
            System.out.println("\n  Current details:\n" + existing.getDisplayInfo());
            System.out.println("\n  Enter new values (press Enter to keep existing):");

            String name = readString("  New Name    : ");
            String ageStr = readString("  New Age     : ");
            String gender = readString("  New Gender  : ");
            String cont = readString("  New Contact : ");
            String hist = readString("  New History : ");

            int newAge = ageStr.isBlank() ? 0 : Integer.parseInt(ageStr);
            service.updatePatient(id, name, newAge, gender, cont, hist);
            System.out.println("\n  [OK] Patient updated successfully.");
        } catch (PatientNotFoundException e) {
            System.out.println("  Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("  Error: Invalid age - must be a number.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // Option 4 - Add a new doctor
    private static void addDoctor() {
        printSection("ADD NEW DOCTOR");
        try {
            String name = readString("Full Name        : ");
            int age = readInt("Age              : ");
            String gender = readString("Gender           : ");
            String cont = readString("Contact          : ");
            String spec = readString("Specialization   : ");
            double fee = readDouble("Consultation Fee : ");
            System.out.println("  Enter available time slots (comma-separated, e.g. 09:00,11:00,14:00):");
            String slotsInput = readString("  Slots: ");
            List<String> slots = Arrays.asList(slotsInput.split(","));
            slots.replaceAll(String::trim);

            Doctor dr = service.addDoctor(name, age, gender, cont, spec, fee, slots);
            System.out.println("\n  [OK] Doctor added successfully!");
            System.out.println(dr.getDisplayInfo());
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // Option 5 - View all doctors and their available slots
    private static void viewDoctors() {
        printSection("DOCTORS & AVAILABILITY");
        List<Doctor> list = service.getAllDoctors();
        if (list.isEmpty()) {
            System.out.println("  No doctors registered yet.");
        } else {
            list.forEach(dr -> {
                System.out.println(dr.getDisplayInfo());
                System.out.println();
            });
        }
    }

    // Option 6 - Book an appointment
    private static void bookAppointment() {
        printSection("BOOK APPOINTMENT");
        try {
            // Show doctor list to help user pick
            viewDoctors();
            String patientId = readString("Patient ID (e.g. P001): ");
            String doctorId = readString("Doctor ID  (e.g. D001): ");
            String slot = readString("Time Slot  (e.g. 09:00): ");

            Appointment appt = service.bookAppointment(patientId, doctorId, slot.trim());
            System.out.println("\n  [OK] Appointment booked!");
            System.out.println(appt.getDisplayInfo());
        } catch (PatientNotFoundException | SlotUnavailableException e) {
            System.out.println("  Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // Option 7 - Cancel a scheduled appointment
    private static void cancelAppointment() {
        printSection("CANCEL APPOINTMENT");
        try {
            String id = readString("Appointment ID (e.g. A001): ");
            service.cancelAppointment(id);
            System.out.println("  [OK] Appointment " + id + " cancelled. Slot restored.");
        } catch (InvalidAppointmentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // Option 8 - View appointments (all, or filter by patient/doctor ID)
    private static void viewAppointments() {
        printSection("VIEW APPOINTMENTS");
        System.out.print("  Filter by Patient/Doctor ID (or press Enter for all): ");
        String filter = scanner.nextLine().trim();

        List<Appointment> list = service.getAppointments(filter);
        if (list.isEmpty()) {
            System.out.println("  No appointments found.");
        } else {
            System.out.println("\n  Found " + list.size() + " appointment(s):\n");
            list.forEach(a -> {
                System.out.println(a.getDisplayInfo());
                System.out.println();
            });
        }
    }

    // Option 9 - Mark appointment complete and print bill
    private static void completeAppointment() {
        printSection("COMPLETE APPOINTMENT & GENERATE BILL");
        try {
            String id = readString("Appointment ID (e.g. A001): ");
            String bill = service.completeAppointment(id);
            System.out.println(bill);
        } catch (InvalidAppointmentException | PatientNotFoundException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // DISPLAY HELPERS

    private static void printBanner() {
        System.out.println("\nHOSPITAL APPOINTMENT MANAGEMENT SYSTEM");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println("\nMAIN MENU");
        System.out.println("  1.  Register Patient");
        System.out.println("  2.  View / Search Patients");
        System.out.println("  3.  Update Patient Details");
        System.out.println("  4.  Add Doctor");
        System.out.println("  5.  View Doctors & Availability");
        System.out.println("  6.  Book Appointment");
        System.out.println("  7.  Cancel Appointment");
        System.out.println("  8.  View Appointments");
        System.out.println("  9.  Complete Appointment & Generate Bill");
        System.out.println("  10. Exit");
        System.out.println();
    }

    private static void printSection(String title) {
        System.out.println("\n" + title);
    }

    // INPUT HELPERS

    // Reads a String from the user.
    private static String readString(String prompt) {
        System.out.print("  " + prompt);
        return scanner.nextLine();
    }

    // Reads an integer; re-prompts if input is not a valid number.
    private static int readInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  Error: Please enter a valid whole number.");
            }
        }
    }

    // Reads a double; re-prompts if input is not a valid number.
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("  Error: Please enter a valid number (e.g. 500.00).");
            }
        }
    }
}
