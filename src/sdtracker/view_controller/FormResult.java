/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

/**
 *
 * @author Tim Smith
 */
public class FormResult {
    private FormResultStatus resultStatus;
    private String message;

    public FormResult(FormResultStatus resultStatus, String message) {
        this.resultStatus = resultStatus;
        this.message = message;
    }

    public FormResultStatus getResultStatus() {
        return resultStatus;
    }

    public String getMessage() {
        return message;
    }
    
    public enum FormResultStatus {
        SUCCESS,
        FAILURE
    }
}
