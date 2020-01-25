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
public class TicketNumberGenerator {
    public static String generateTicketNumber() {
        String ticketNumber;
        String timestamp = LocalDateTime.now().format(DateTimeHandler.DATE_TIME_STAMP);
        
        ticketNumber = "T" + timestamp;
        
        return ticketNumber;
    }
}
