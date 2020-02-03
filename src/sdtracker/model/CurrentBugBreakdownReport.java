/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

/**
 *
 * @author Tim Smith
 */
public class CurrentBugBreakdownReport {
    private int newBugCount;
    private int openBugCount;
    private int fixInDevelopmentBugCount;
    private int fixPendingTestingBugCount;
    private int resolvedBugCount;
    private int onHoldBugCount;
    private int criticalBugCount;
    private int urgentBugCount;
    private int normalBugCount;
    private int lowBugCount;
    private double newBugAgeInSeconds;
    private double openBugAgeInSeconds;
    private double fixInDevelopmentBugAgeInSeconds;
    private double fixPendingTestingBugAgeInSeconds;
    private double resolvedBugAgeInSeconds;
    private double onHoldBugAgeInSeconds;
    private double criticalBugAgeInSeconds;
    private double urgentBugAgeInSeconds;
    private double normalBugAgeInSeconds;
    private double lowBugAgeInSeconds;

    public CurrentBugBreakdownReport() {
    }
    
    public int getTotalBugs() {
        return newBugCount + openBugCount + fixInDevelopmentBugCount 
             + fixPendingTestingBugCount + resolvedBugCount + onHoldBugCount;
    }
    
    public double getNewBugPercentage() {
        return getPercentage(newBugCount);
    }
    public double getOpenBugPercentage() {
        return getPercentage(openBugCount);
    }
    
    public double getResolvedBugPercentage() {
        return getPercentage(resolvedBugCount);
    }
    
    public double getOnHoldBugPercentage() {
        return getPercentage(onHoldBugCount);
    }
    
    public double getCriticalBugPercentage() {
        return getPercentage(criticalBugCount);
    }
    public double getUrgentBugPercentage() {
        return getPercentage(urgentBugCount);
    }
    
    public double getNormalBugPercentage() {
        return getPercentage(normalBugCount);
    }
    
    public double getLowBugPercentage() {
        return getPercentage(lowBugCount);
    }
    
    public double getPercentage(int count) {
        return count / (double) getTotalBugs();
    }

    public int getNewBugCount() {
        return newBugCount;
    }

    public void setNewBugCount(int newBugCount) {
        this.newBugCount = newBugCount;
    }

    public int getOpenBugCount() {
        return openBugCount;
    }

    public void setOpenBugCount(int openBugCount) {
        this.openBugCount = openBugCount;
    }

    public int getFixInDevelopmentBugCount() {
        return fixInDevelopmentBugCount;
    }

    public void setFixInDevelopmentBugCount(int fixInDevelopmentBugCount) {
        this.fixInDevelopmentBugCount = fixInDevelopmentBugCount;
    }

    public int getFixPendingTestingBugCount() {
        return fixPendingTestingBugCount;
    }

    public void setFixPendingTestingBugCount(int fixPendingTestingBugCount) {
        this.fixPendingTestingBugCount = fixPendingTestingBugCount;
    }
    
    public int getResolvedBugCount() {
        return resolvedBugCount;
    }

    public void setResolvedBugCount(int resolvedBugCount) {
        this.resolvedBugCount = resolvedBugCount;
    }

    public int getOnHoldBugCount() {
        return onHoldBugCount;
    }

    public void setOnHoldBugCount(int onHoldBugCount) {
        this.onHoldBugCount = onHoldBugCount;
    }

    public int getCriticalBugCount() {
        return criticalBugCount;
    }

    public void setCriticalBugCount(int criticalBugCount) {
        this.criticalBugCount = criticalBugCount;
    }

    public int getUrgentBugCount() {
        return urgentBugCount;
    }

    public void setUrgentBugCount(int urgentBugCount) {
        this.urgentBugCount = urgentBugCount;
    }

    public int getNormalBugCount() {
        return normalBugCount;
    }

    public void setNormalBugCount(int normalBugCount) {
        this.normalBugCount = normalBugCount;
    }

    public int getLowBugCount() {
        return lowBugCount;
    }

    public void setLowBugCount(int lowBugCount) {
        this.lowBugCount = lowBugCount;
    }

    public double getNewBugAgeInSeconds() {
        return newBugAgeInSeconds;
    }

    public void setNewBugAgeInSeconds(double newBugAgeInSeconds) {
        this.newBugAgeInSeconds = newBugAgeInSeconds;
    }

    public double getOpenBugAgeInSeconds() {
        return openBugAgeInSeconds;
    }

    public void setOpenBugAgeInSeconds(double openBugAgeInSeconds) {
        this.openBugAgeInSeconds = openBugAgeInSeconds;
    }

    public double getFixInDevelopmentBugAgeInSeconds() {
        return fixInDevelopmentBugAgeInSeconds;
    }

    public void setFixInDevelopmentBugAgeInSeconds(double fixInDevelopmentBugAgeInSeconds) {
        this.fixInDevelopmentBugAgeInSeconds = fixInDevelopmentBugAgeInSeconds;
    }

    public double getFixPendingTestingBugAgeInSeconds() {
        return fixPendingTestingBugAgeInSeconds;
    }

    public void setFixPendingTestingBugAgeInSeconds(double fixPendingTestingBugAgeInSeconds) {
        this.fixPendingTestingBugAgeInSeconds = fixPendingTestingBugAgeInSeconds;
    }
    
    public double getResolvedBugAgeInSeconds() {
        return resolvedBugAgeInSeconds;
    }

    public void setResolvedBugAgeInSeconds(double resolvedBugAgeInSeconds) {
        this.resolvedBugAgeInSeconds = resolvedBugAgeInSeconds;
    }

    public double getOnHoldBugAgeInSeconds() {
        return onHoldBugAgeInSeconds;
    }

    public void setOnHoldBugAgeInSeconds(double onHoldBugAgeInSeconds) {
        this.onHoldBugAgeInSeconds = onHoldBugAgeInSeconds;
    }

    public double getCriticalBugAgeInSeconds() {
        return criticalBugAgeInSeconds;
    }

    public void setCriticalBugAgeInSeconds(double criticalBugAgeInSeconds) {
        this.criticalBugAgeInSeconds = criticalBugAgeInSeconds;
    }

    public double getUrgentBugAgeInSeconds() {
        return urgentBugAgeInSeconds;
    }

    public void setUrgentBugAgeInSeconds(double urgentBugAgeInSeconds) {
        this.urgentBugAgeInSeconds = urgentBugAgeInSeconds;
    }

    public double getNormalBugAgeInSeconds() {
        return normalBugAgeInSeconds;
    }

    public void setNormalBugAgeInSeconds(double normalBugAgeInSeconds) {
        this.normalBugAgeInSeconds = normalBugAgeInSeconds;
    }

    public double getLowBugAgeInSeconds() {
        return lowBugAgeInSeconds;
    }

    public void setLowBugAgeInSeconds(double lowBugAgeInSeconds) {
        this.lowBugAgeInSeconds = lowBugAgeInSeconds;
    }
}
