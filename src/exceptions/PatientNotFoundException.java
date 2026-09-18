package exceptions;

// Thrown when a requested patient is not found in the system.
public class PatientNotFoundException extends Exception {

    public PatientNotFoundException(String message) {
        super(message);
    }
}
