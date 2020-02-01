/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Tim Smith
 */
public final class TeamStatBoard {

    private final IntegerProperty totalNotClosedTickets = new SimpleIntegerProperty();
    private final IntegerProperty totalOpenTickets = new SimpleIntegerProperty();
    private final IntegerProperty totalNotClosedBugs = new SimpleIntegerProperty();
    private final IntegerProperty totalOpenBugs = new SimpleIntegerProperty();
    private final DoubleProperty averageTicketResolutionTime = new SimpleDoubleProperty();
    private final DoubleProperty averageBugResolutionTime = new SimpleDoubleProperty();

    public TeamStatBoard() {
    }
    
    public TeamStatBoard(int totalNotClosedTickets, int totalOpenTickets, int totalNotClosedBugs, int totalOpenBugs) {
        setTotalNotClosedTickets(totalNotClosedBugs);
        setTotalOpenTickets(totalOpenTickets);
        setTotalNotClosedBugs(totalNotClosedBugs);
        setTotalOpenBugs(totalOpenBugs);
    }
    
    public int getTotalNotClosedTickets() {
        return totalNotClosedTickets.get();
    }

    public void setTotalNotClosedTickets(int value) {
        totalNotClosedTickets.set(value);
    }

    public IntegerProperty totalNotClosedTicketsProperty() {
        return totalNotClosedTickets;
    }
    
    public int getTotalOpenTickets() {
        return totalOpenTickets.get();
    }

    public void setTotalOpenTickets(int value) {
        totalOpenTickets.set(value);
    }

    public IntegerProperty totalOpenTicketsProperty() {
        return totalOpenTickets;
    }

    public int getTotalNotClosedBugs() {
        return totalNotClosedBugs.get();
    }

    public void setTotalNotClosedBugs(int value) {
        totalNotClosedBugs.set(value);
    }

    public IntegerProperty totalNotClosedBugsProperty() {
        return totalNotClosedBugs;
    }

    public int getTotalOpenBugs() {
        return totalOpenTickets.get();
    }

    public void setTotalOpenBugs(int value) {
        totalOpenTickets.set(value);
    }
    
    public IntegerProperty getTotalOpenBugsProperty() {
        return totalOpenBugs;
    }
    
    public double getAverageTicketResolutionTime() {
        return averageTicketResolutionTime.get();
    }

    public void setAverageTicketResolutionTime(double value) {
        averageTicketResolutionTime.set(value);
    }

    public DoubleProperty averageTicketResolutionTimeProperty() {
        return averageTicketResolutionTime;
    }
    
    public double getAverageBugResolutionTime() {
        return averageBugResolutionTime.get();
    }

    public void setAverageBugResolutionTime(double value) {
        averageBugResolutionTime.set(value);
    }

    public DoubleProperty averageBugResolutionTimeProperty() {
        return averageBugResolutionTime;
    }
    
}
