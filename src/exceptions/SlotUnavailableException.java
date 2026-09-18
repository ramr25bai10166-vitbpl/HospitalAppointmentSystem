package exceptions;

// Thrown when a requested time slot is already booked
// or is not in the doctor's available slots list.
public class SlotUnavailableException extends Exception {

    public SlotUnavailableException(String message) {
        super(message);
    }
}
