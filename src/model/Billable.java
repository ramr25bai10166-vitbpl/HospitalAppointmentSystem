package model;

// Interface that any billable entity must implement.
// Demonstrates the use of interfaces in OOP (abstraction contract).
// Currently implemented by HospitalService to generate consultation bills.
public interface Billable {

    // Generates and returns a formatted bill string.
    // appointment - the completed appointment
    // patient     - the patient involved
    // doctor      - the doctor who provided the consultation
    String generateBill(Appointment appointment, Patient patient, Doctor doctor);
}
