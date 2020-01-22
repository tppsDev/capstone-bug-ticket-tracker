/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.utility;

/**
 *
 * @author Tim Smith
 */
public class ValidationPattern {

    /**
     * This pattern matches 1234567890 or 123-456-7890
     * or (123)456-7890 or (123)4567890
     */
    public static final String PHONE = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";

    /**
     * This pattern matches on valid email addresses. 
     */
    public static final String EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    
    /**
     * This pattern matches 12345 or 12345-1234
     */
    public static final String ZIPCODE = "^[0-9]{5}(?:-[0-9]{4})?$";
    
    /**
     * This pattern matches AB12345678
     */
    public static final String ASSET_NUMBER = "^[A-Z]{2}\\d{8}$";
    
    /**
     * This pattern matches A123456789
     */
    public static final String TICKET_NUMBER = "^[A-Z]{1}\\d{9}$";
    
    /**
     * This pattern matches ABC1234567
     */
    public static final String BUG_NUMBER = "^[A-Z]{3}\\d{7}$";
    
    /**
     * This pattern matches 8-20 characters (a-z, 1-9 plus . and _). Cannot begin or end with . or _
     */
    public static final String USER_NAME = "^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-z0-9._]+(?<![_.])$";
}
