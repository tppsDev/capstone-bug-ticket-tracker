/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author Tim Smith
 */
public class Ticket {
    private int id;
    private String title;
    private String description;
    private Product product;
    private AppUser contact;
    private AppUser assignedAppUser;
    private LocalDateTime createdTimestamp;
    private AppUser createdByAppUser;
    private LocalDateTime lastUpdatedTimestamp;
    private AppUser lastUpdatedByAppUser;
    private String ticketNumber;
    private Asset asset;
    private TicketStatus status;
    private TicketPriority priority;

    public Ticket() {
    }
    
    public Ticket(int id) {
        this.id = id;
    }
    

    public Ticket(int id, String title, String description, Product product, AppUser contact, AppUser assignedAppUser, 
            LocalDateTime createdTimestamp, AppUser createdByAppUser, LocalDateTime lastupdatedTimestamp, 
            AppUser lastUpdatedByAppUser, String ticketNumber, Asset asset, 
            TicketStatus ticketStatus, TicketPriority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.product = product;
        this.contact = contact;
        this.assignedAppUser = assignedAppUser;
        this.createdTimestamp = createdTimestamp;
        this.createdByAppUser = createdByAppUser;
        this.lastUpdatedTimestamp = lastupdatedTimestamp;
        this.lastUpdatedByAppUser = lastUpdatedByAppUser;
        this.ticketNumber = ticketNumber;
        this.asset = asset;
        this.status = ticketStatus;
        this.priority = priority;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public AppUser getContact() {
        return contact;
    }

    public void setContact(AppUser contact) {
        this.contact = contact;
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

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return ticketNumber;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        hash = 29 * hash + Objects.hashCode(this.ticketNumber);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ticket other = (Ticket) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
}
