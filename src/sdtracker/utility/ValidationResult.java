/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.utility;

/**
 *
 * @author Tim Smith
 */
public class ValidationResult {
    private boolean valid;
    private String message;

    /**
     *  Default constructor is used to create a positive validation 
     *  result of true with a message of "Success". To create a 
     * result other than this, use:
     *          ValidationResult(boolean, String) constructor.
     */
    public ValidationResult() {
        valid = true;
        message = "Success!";
    }
    
    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
    
}
