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
public final class UserStatBoard {
    private final IntegerProperty totalNotClosedTickets = new SimpleIntegerProperty();
    private final IntegerProperty totalOpenTickets = new SimpleIntegerProperty();
    private final IntegerProperty totalNotClosedBugs = new SimpleIntegerProperty();
    private final IntegerProperty totalOpenBugs = new SimpleIntegerProperty();
    private final DoubleProperty averageTicketResolutionTime = new SimpleDoubleProperty();
    private final DoubleProperty averageBugResolutionTime = new SimpleDoubleProperty();

    public UserStatBoard() {
    }
    
    public UserStatBoard(int totalNotClosedTickets, int totalOpenTickets, int totalNotClosedBugs, int totalOpenBugs) {
        setTotalNotClosedTickets(totalNotClosedTickets);
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
        return totalOpenBugs.get();
    }

    public void setTotalOpenBugs(int value) {
        totalOpenBugs.set(value);
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
    
    @Override
    public String toString() {
        return "Assigned Tickets: " + String.valueOf(getTotalOpenTickets()) + "\n"
              +"Assigned Bugs:    " + String.valueOf(getTotalOpenBugs());
    }
}
