/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

/**
 *
 * @author Tim Smith
 */
public class CurrentTicketBreakdownReport {
    private int newTicketCount;
    private int openTicketCount;
    private int resolvedTicketCount;
    private int onHoldTicketCount;
    private int criticalTicketCount;
    private int urgentTicketCount;
    private int normalTicketCount;
    private int lowTicketCount;
    private double newTicketAgeInSeconds;
    private double openTicketAgeInSeconds;
    private double resolvedTicketAgeInSeconds;
    private double onHoldTicketAgeInSeconds;
    private double criticalTicketAgeInSeconds;
    private double urgentTicketAgeInSeconds;
    private double normalTicketAgeInSeconds;
    private double lowTicketAgeInSeconds;

    public CurrentTicketBreakdownReport() {
    }
    
    public int getTotalTickets() {
        return newTicketCount + openTicketCount + resolvedTicketCount + onHoldTicketCount;
    }
    
    public double getNewTicketPercentage() {
        return getPercentage(newTicketCount);
    }
    public double getOpenTicketPercentage() {
        return getPercentage(openTicketCount);
    }
    
    public double getResolvedTicketPercentage() {
        return getPercentage(resolvedTicketCount);
    }
    
    public double getOnHoldTicketPercentage() {
        return getPercentage(onHoldTicketCount);
    }
    
    public double getCriticalTicketPercentage() {
        return getPercentage(criticalTicketCount);
    }
    public double getUrgentTicketPercentage() {
        return getPercentage(urgentTicketCount);
    }
    
    public double getNormalTicketPercentage() {
        return getPercentage(normalTicketCount);
    }
    
    public double getLowTicketPercentage() {
        return getPercentage(lowTicketCount);
    }
    
    public double getPercentage(int count) {
        return count / (double) getTotalTickets();
    }

    public int getNewTicketCount() {
        return newTicketCount;
    }

    public void setNewTicketCount(int newTicketCount) {
        this.newTicketCount = newTicketCount;
    }

    public int getOpenTicketCount() {
        return openTicketCount;
    }

    public void setOpenTicketCount(int openTicketCount) {
        this.openTicketCount = openTicketCount;
    }

    public int getResolvedTicketCount() {
        return resolvedTicketCount;
    }

    public void setResolvedTicketCount(int resolvedTicketCount) {
        this.resolvedTicketCount = resolvedTicketCount;
    }

    public int getOnHoldTicketCount() {
        return onHoldTicketCount;
    }

    public void setOnHoldTicketCount(int onHoldTicketCount) {
        this.onHoldTicketCount = onHoldTicketCount;
    }

    public int getCriticalTicketCount() {
        return criticalTicketCount;
    }

    public void setCriticalTicketCount(int criticalTicketCount) {
        this.criticalTicketCount = criticalTicketCount;
    }

    public int getUrgentTicketCount() {
        return urgentTicketCount;
    }

    public void setUrgentTicketCount(int urgentTicketCount) {
        this.urgentTicketCount = urgentTicketCount;
    }

    public int getNormalTicketCount() {
        return normalTicketCount;
    }

    public void setNormalTicketCount(int normalTicketCount) {
        this.normalTicketCount = normalTicketCount;
    }

    public int getLowTicketCount() {
        return lowTicketCount;
    }

    public void setLowTicketCount(int lowTicketCount) {
        this.lowTicketCount = lowTicketCount;
    }

    public double getNewTicketAgeInSeconds() {
        return newTicketAgeInSeconds;
    }

    public void setNewTicketAgeInSeconds(double newTicketAgeInSeconds) {
        this.newTicketAgeInSeconds = newTicketAgeInSeconds;
    }

    public double getOpenTicketAgeInSeconds() {
        return openTicketAgeInSeconds;
    }

    public void setOpenTicketAgeInSeconds(double openTicketAgeInSeconds) {
        this.openTicketAgeInSeconds = openTicketAgeInSeconds;
    }

    public double getResolvedTicketAgeInSeconds() {
        return resolvedTicketAgeInSeconds;
    }

    public void setResolvedTicketAgeInSeconds(double resolvedTicketAgeInSeconds) {
        this.resolvedTicketAgeInSeconds = resolvedTicketAgeInSeconds;
    }

    public double getOnHoldTicketAgeInSeconds() {
        return onHoldTicketAgeInSeconds;
    }

    public void setOnHoldTicketAgeInSeconds(double onHoldTicketAgeInSeconds) {
        this.onHoldTicketAgeInSeconds = onHoldTicketAgeInSeconds;
    }

    public double getCriticalTicketAgeInSeconds() {
        return criticalTicketAgeInSeconds;
    }

    public void setCriticalTicketAgeInSeconds(double criticalTicketAgeInSeconds) {
        this.criticalTicketAgeInSeconds = criticalTicketAgeInSeconds;
    }

    public double getUrgentTicketAgeInSeconds() {
        return urgentTicketAgeInSeconds;
    }

    public void setUrgentTicketAgeInSeconds(double urgentTicketAgeInSeconds) {
        this.urgentTicketAgeInSeconds = urgentTicketAgeInSeconds;
    }

    public double getNormalTicketAgeInSeconds() {
        return normalTicketAgeInSeconds;
    }

    public void setNormalTicketAgeInSeconds(double normalTicketAgeInSeconds) {
        this.normalTicketAgeInSeconds = normalTicketAgeInSeconds;
    }

    public double getLowTicketAgeInSeconds() {
        return lowTicketAgeInSeconds;
    }

    public void setLowTicketAgeInSeconds(double lowTicketAgeInSeconds) {
        this.lowTicketAgeInSeconds = lowTicketAgeInSeconds;
    }
    
    
}
