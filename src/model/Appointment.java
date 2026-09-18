package model;

// Represents a hospital appointment linking a Patient, a Doctor, and a time slot.
// Status can be SCHEDULED, COMPLETED, or CANCELLED.
public class Appointment {

    // Possible states of an appointment
    public enum Status { SCHEDULED, COMPLETED, CANCELLED }

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String timeSlot;
    private String date;
    private Status status;
    private double billAmount; // Populated when status moves to COMPLETED

    // Constructor - new appointments always start as SCHEDULED
    public Appointment(String appointmentId, String patientId,
                       String doctorId, String timeSlot, String date) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.timeSlot = timeSlot;
        this.date = date;
        this.status = Status.SCHEDULED;
        this.billAmount = 0.0;
    }

    // Getters and Setters

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getTimeSlot() { return timeSlot; }
    public String getDate() { return date; }
    public Status getStatus() { return status; }
    public double getBillAmount() { return billAmount; }

    public void setStatus(Status status) { this.status = status; }
    public void setBillAmount(double amount) { this.billAmount = amount; }

    // Formats appointment details for console display.
    public String getDisplayInfo() {
        return String.format(
            "  Appt ID   : %s%n" +
            "  Patient ID: %s%n" +
            "  Doctor ID : %s%n" +
            "  Date/Slot : %s at %s%n" +
            "  Status    : %s%n" +
            "  Bill      : Rs. %.2f",
            appointmentId, patientId, doctorId,
            date, timeSlot, status, billAmount
        );
    }

    // Saves appointment to a pipe-delimited line for file storage.
    // Format: appointmentId|patientId|doctorId|timeSlot|date|status|billAmount
    public String toCsvLine() {
        return String.join("|",
            appointmentId, patientId, doctorId,
            timeSlot, date, status.name(),
            String.valueOf(billAmount)
        );
    }

    // Reads a pipe-delimited line and returns an Appointment object.
    public static Appointment fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        Appointment appt = new Appointment(
            parts[0], parts[1], parts[2], parts[3], parts[4]
        );
        appt.setStatus(Status.valueOf(parts[5]));
        appt.setBillAmount(Double.parseDouble(parts[6]));
        return appt;
    }
}
