/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.utility;

import java.time.LocalDateTime;

/**
 *
 * @author Tim Smith
 */
public class BugNumberGenerator {
    // Current version simplified, future versions will define different meanings to first 3 characters
    public static String generateBugNumber() {
        String bugNumber;
        String timestamp = LocalDateTime.now().format(DateTimeHandler.DATE_TIME_STAMP);
        
        bugNumber = "BTI" + timestamp;
        
        return bugNumber;
    }
    
}
