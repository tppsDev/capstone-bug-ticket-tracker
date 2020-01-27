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
public class AssetNumberGenerator {
    public static String generateAssetNumber() {
        String assetNumber;
        String timestamp = LocalDateTime.now().format(DateTimeHandler.DATE_TIME_STAMP);
        
        assetNumber = "AT" + timestamp;
        
        return assetNumber;
    }
}
