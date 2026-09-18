package exceptions;

// Thrown for invalid appointment operations, for example:
// - Trying to cancel an already-completed appointment
// - Trying to complete an appointment that is already cancelled
// - Appointment ID not found
public class InvalidAppointmentException extends Exception {

    public InvalidAppointmentException(String message) {
        super(message);
    }
}
