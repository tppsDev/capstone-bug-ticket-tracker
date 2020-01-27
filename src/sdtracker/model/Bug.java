/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

import java.time.LocalDateTime;

/**
 *
 * @author Tim Smith
 */
public class Bug {
    private int id;
    private String title;
    private String description;
    private BugStatus status;
    private AppUser assignedAppUser;
    private LocalDateTime createdTimestamp;
    private AppUser createdByAppUser;
    private LocalDateTime lastUpdatedTimestamp;
    private AppUser lastUpdatedByAppUser;
    private Product product;
    private Contact contact;
    private BugPriority priority;
    private String bugNumber;

    public Bug() {
    }

    public Bug(int id) {
        this.id = id;
    }

    public Bug(int id, String title, String description, BugStatus status, AppUser assignedAppUser, 
            LocalDateTime createdTimestamp, AppUser createdByAppUser, LocalDateTime lastUpdatedTimestamp, 
            AppUser lastUpdatedByAppUser, Product product, Contact contact, BugPriority priority, String bugNumber) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.assignedAppUser = assignedAppUser;
        this.createdTimestamp = createdTimestamp;
        this.createdByAppUser = createdByAppUser;
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
        this.lastUpdatedByAppUser = lastUpdatedByAppUser;
        this.product = product;
        this.contact = contact;
        this.priority = priority;
        this.bugNumber = bugNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BugStatus getStatus() {
        return status;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }

    public AppUser getAssignedAppUser() {
        return assignedAppUser;
    }

    public void setAssignedAppUser(AppUser assignedAppUser) {
        this.assignedAppUser = assignedAppUser;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public AppUser getCreatedByAppUser() {
        return createdByAppUser;
    }

    public void setCreatedByAppUser(AppUser createdByAppUser) {
        this.createdByAppUser = createdByAppUser;
    }

    public LocalDateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(LocalDateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public AppUser getLastUpdatedByAppUser() {
        return lastUpdatedByAppUser;
    }

    public void setLastUpdatedByAppUser(AppUser lastUpdatedByAppUser) {
        this.lastUpdatedByAppUser = lastUpdatedByAppUser;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public BugPriority getPriority() {
        return priority;
    }

    public void setPriority(BugPriority priority) {
        this.priority = priority;
    }

    public String getBugNumber() {
        return bugNumber;
    }

    public void setBugNumber(String bugNumber) {
        this.bugNumber = bugNumber;
    }
    
}
