package service;

import exceptions.InvalidAppointmentException;
import exceptions.PatientNotFoundException;
import exceptions.SlotUnavailableException;
import model.Appointment;
import model.Appointment.Status;
import model.Billable;
import model.Doctor;
import model.Patient;
import storage.FileHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// HospitalService is the central business-logic layer.
// It manages in-memory lists of Patients, Doctors, and Appointments
// and delegates all persistence to FileHandler.
// It also implements the Billable interface to generate consultation bills.
public class HospitalService implements Billable {

    // In-memory data stores
    private List<Patient> patients;
    private List<Doctor> doctors;
    private List<Appointment> appointments;

    // Simple counters for auto-generating IDs
    private int patientCounter;
    private int doctorCounter;
    private int appointmentCounter;

    // Constructor - loads existing data from files on startup.
    public HospitalService() {
        FileHandler.initDataDirectory();
        patients = FileHandler.loadPatients();
        doctors = FileHandler.loadDoctors();
        appointments = FileHandler.loadAppointments();

        // Seed counters from loaded data so IDs never collide
        patientCounter = patients.stream()
            .mapToInt(p -> extractNumber(p.getId())).max().orElse(0);
        doctorCounter = doctors.stream()
            .mapToInt(d -> extractNumber(d.getId())).max().orElse(0);
        appointmentCounter = appointments.stream()
            .mapToInt(a -> extractNumber(a.getAppointmentId())).max().orElse(0);
    }

    // Extracts the numeric suffix from an ID like "P003" to get 3
    private int extractNumber(String id) {
        try {
            return Integer.parseInt(id.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ID generators

    private String nextPatientId() { return String.format("P%03d", ++patientCounter); }
    private String nextDoctorId() { return String.format("D%03d", ++doctorCounter); }
    private String nextAppointmentId() { return String.format("A%03d", ++appointmentCounter); }

    // PATIENT OPERATIONS

    // Registers a new patient after validating inputs.
    // Throws IllegalArgumentException if age is invalid or required fields are blank.
    public Patient registerPatient(String name, int age, String gender,
                                   String contact, String medicalHistory) {
        // Input validation
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Patient name cannot be empty.");
        if (age < 0 || age > 150)
            throw new IllegalArgumentException("Age must be between 0 and 150.");
        if (gender == null || gender.isBlank())
            throw new IllegalArgumentException("Gender cannot be empty.");
        if (contact == null || contact.isBlank())
            throw new IllegalArgumentException("Contact cannot be empty.");

        Patient patient = new Patient(
            nextPatientId(), name.trim(), age,
            gender.trim(), contact.trim(),
            medicalHistory == null ? "None" : medicalHistory.trim()
        );
        patients.add(patient);
        FileHandler.savePatients(patients);
        return patient;
    }

    // Returns the full list of registered patients.
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    // Searches patients by name (case-insensitive partial match) or exact ID.
    public List<Patient> searchPatients(String query) {
        String q = query.trim().toLowerCase();
        return patients.stream()
            .filter(p -> p.getId().equalsIgnoreCase(q)
                      || p.getName().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    // Finds a patient by ID. Throws PatientNotFoundException if not found.
    public Patient findPatientById(String id) throws PatientNotFoundException {
        return patients.stream()
            .filter(p -> p.getId().equalsIgnoreCase(id.trim()))
            .findFirst()
            .orElseThrow(() -> new PatientNotFoundException(
                "No patient found with ID: " + id));
    }

    // Updates an existing patient's details.
    // Pass null or blank for any field you do not want to change.
    public void updatePatient(String id, String newName, int newAge,
                              String newGender, String newContact,
                              String newHistory) throws PatientNotFoundException {
        Patient p = findPatientById(id);
        if (newName != null && !newName.isBlank()) p.setName(newName.trim());
        if (newAge > 0 && newAge <= 150) p.setAge(newAge);
        if (newGender != null && !newGender.isBlank()) p.setGender(newGender.trim());
        if (newContact != null && !newContact.isBlank()) p.setContact(newContact.trim());
        if (newHistory != null && !newHistory.isBlank()) p.setMedicalHistory(newHistory.trim());
        FileHandler.savePatients(patients);
    }

    // DOCTOR OPERATIONS

    // Adds a new doctor to the system.
    public Doctor addDoctor(String name, int age, String gender, String contact,
                            String specialization, double fee, List<String> slots) {
        // Input validation
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Doctor name cannot be empty.");
        if (age < 0 || age > 150)
            throw new IllegalArgumentException("Age must be between 0 and 150.");
        if (specialization == null || specialization.isBlank())
            throw new IllegalArgumentException("Specialization cannot be empty.");
        if (fee < 0)
            throw new IllegalArgumentException("Consultation fee cannot be negative.");
        if (slots == null || slots.isEmpty())
            throw new IllegalArgumentException("Doctor must have at least one available slot.");

        Doctor doctor = new Doctor(
            nextDoctorId(), name.trim(), age, gender.trim(),
            contact.trim(), specialization.trim(), fee, slots
        );
        doctors.add(doctor);
        FileHandler.saveDoctors(doctors);
        return doctor;
    }

    // Returns the full list of doctors.
    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors);
    }

    // Finds a doctor by ID. Returns Optional.empty() if not found.
    public Optional<Doctor> findDoctorById(String id) {
        return doctors.stream()
            .filter(d -> d.getId().equalsIgnoreCase(id.trim()))
            .findFirst();
    }

    // APPOINTMENT OPERATIONS

    // Books an appointment after validating patient, doctor, and slot availability.
    // Throws PatientNotFoundException if patientId does not exist.
    // Throws SlotUnavailableException if the slot is already booked or not listed.
    public Appointment bookAppointment(String patientId, String doctorId, String slot)
            throws PatientNotFoundException, SlotUnavailableException {

        // Validate patient exists
        Patient patient = findPatientById(patientId);

        // Validate doctor exists
        Doctor dr = findDoctorById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No doctor found with ID: " + doctorId));

        // Validate slot is in the doctor's available list
        if (!dr.hasSlot(slot)) {
            throw new SlotUnavailableException(
                "Slot '" + slot + "' is not available for Dr. " + dr.getName()
                + ". Available: " + dr.getAvailableSlots());
        }

        // Remove the slot so no one else can double-book it
        dr.removeSlot(slot);

        String date = LocalDate.now().toString();
        Appointment appt = new Appointment(
            nextAppointmentId(), patient.getId(), dr.getId(), slot, date
        );
        appointments.add(appt);

        // Persist both updated doctor slots and new appointment
        FileHandler.saveDoctors(doctors);
        FileHandler.saveAppointments(appointments);

        return appt;
    }

    // Cancels a SCHEDULED appointment and restores the doctor's slot.
    // Throws InvalidAppointmentException if not found or already completed/cancelled.
    public void cancelAppointment(String appointmentId) throws InvalidAppointmentException {
        Appointment appt = findAppointmentById(appointmentId);

        if (appt.getStatus() != Status.SCHEDULED) {
            throw new InvalidAppointmentException(
                "Cannot cancel appointment " + appointmentId
                + " - current status is: " + appt.getStatus());
        }

        appt.setStatus(Status.CANCELLED);

        // Restore the slot back to the doctor
        findDoctorById(appt.getDoctorId()).ifPresent(d -> d.addSlot(appt.getTimeSlot()));

        FileHandler.saveDoctors(doctors);
        FileHandler.saveAppointments(appointments);
    }

    // Returns all appointments, or filters by patientId or doctorId if provided.
    // Pass null or blank to get all appointments.
    public List<Appointment> getAppointments(String filterById) {
        if (filterById == null || filterById.isBlank()) {
            return new ArrayList<>(appointments);
        }
        String id = filterById.trim().toUpperCase();
        return appointments.stream()
            .filter(a -> a.getPatientId().equalsIgnoreCase(id)
                      || a.getDoctorId().equalsIgnoreCase(id))
            .collect(Collectors.toList());
    }

    // Marks a SCHEDULED appointment as COMPLETED and generates the bill.
    // Throws InvalidAppointmentException if not found or not in SCHEDULED state.
    // Throws PatientNotFoundException if the patient record is missing.
    public String completeAppointment(String appointmentId)
            throws InvalidAppointmentException, PatientNotFoundException {

        Appointment appt = findAppointmentById(appointmentId);

        if (appt.getStatus() != Status.SCHEDULED) {
            throw new InvalidAppointmentException(
                "Appointment " + appointmentId + " cannot be completed - status: "
                + appt.getStatus());
        }

        Doctor dr = findDoctorById(appt.getDoctorId())
            .orElseThrow(() -> new InvalidAppointmentException(
                "Doctor for appointment " + appointmentId + " not found."));

        Patient p = findPatientById(appt.getPatientId());

        // Set consultation fee as bill amount
        appt.setBillAmount(dr.getConsultationFee());
        appt.setStatus(Status.COMPLETED);
        FileHandler.saveAppointments(appointments);

        // Generate and return the formatted bill (via Billable interface)
        return generateBill(appt, p, dr);
    }

    // Billable interface implementation

    // Generates a formatted consultation bill string.
    // Implements the Billable interface.
    @Override
    public String generateBill(Appointment appointment, Patient patient, Doctor doctor) {
        String dateSlot = appointment.getDate() + " at " + appointment.getTimeSlot();
        return "\nCONSULTATION BILL\n\n" +
            "  Appointment ID   : " + appointment.getAppointmentId() + "\n" +
            "  Date / Slot      : " + dateSlot + "\n\n" +
            "  Patient          : " + patient.getName() + "\n" +
            "  Patient ID       : " + patient.getId() + "\n\n" +
            "  Doctor           : Dr. " + doctor.getName() + "\n" +
            "  Specialization   : " + doctor.getSpecialization() + "\n\n" +
            String.format("  Consultation Fee : Rs. %.2f%n", appointment.getBillAmount()) +
            String.format("  GST (18%%)        : Rs. %.2f%n", appointment.getBillAmount() * 0.18) +
            String.format("  Total Payable    : Rs. %.2f%n", appointment.getBillAmount() * 1.18) +
            "\n  Thank you for visiting City Clinic!\n";
    }

    // Helper method

    // Finds an appointment by ID or throws InvalidAppointmentException.
    private Appointment findAppointmentById(String id) throws InvalidAppointmentException {
        return appointments.stream()
            .filter(a -> a.getAppointmentId().equalsIgnoreCase(id.trim()))
            .findFirst()
            .orElseThrow(() -> new InvalidAppointmentException(
                "No appointment found with ID: " + id));
    }
}
