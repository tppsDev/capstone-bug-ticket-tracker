/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

/**
 *
 * @author Tim Smith
 */
public class BugStatus {
    private int id;
    private String name;

    public BugStatus() {
    }

    public BugStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}