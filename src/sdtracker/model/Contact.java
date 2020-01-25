/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

/**
 *
 * @author Tim Smith
 */
public class Contact {
    private int id;
    private String firstName;
    private String lastName;
    private String midInitial;
    private String email;
    private String phone;
    private String phoneType;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zipcode;
    private String company;
    private String jobTitle;
    private String courtesyTitle;
    private ContactType contactType;

    public Contact() {
    }

    public Contact(int id) {
        this.id = id;
    }

    public Contact(int id, String firstName, String lastName, String email, String phone, String phoneType, String courtesyTitle) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.phoneType = phoneType;
        this.courtesyTitle = courtesyTitle;
    }
    

    public Contact(int id, String firstName, String lastName, String midInit, String email, String phone, 
            String phoneType, String address, String address2, String city, String state, String zipcode, 
            String company, String jobTitle, String courtesyTitle, ContactType contactType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.midInitial = midInit;
        this.email = email;
        this.phone = phone;
        this.phoneType = phoneType;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.company = company;
        this.jobTitle = jobTitle;
        this.courtesyTitle = courtesyTitle;
        this.contactType = contactType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMidInit() {
        return midInitial;
    }

    public void setMidInit(String midInitial) {
        this.midInitial = midInitial;
    }
    
    public String getDisplayName() {
        String displayName = "";
        
        if (!courtesyTitle.isEmpty()) {
            displayName = displayName + courtesyTitle;
        }
        
        if (this.midInitial.isEmpty()) {
            displayName = displayName + " " + firstName + " " + lastName;
        } else {
            displayName = displayName + " " + firstName + " " + midInitial + " " + lastName;
        }
        
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCourtesyTitle() {
        return courtesyTitle;
    }

    public void setCourtesyTitle(String courtesyTitle) {
        this.courtesyTitle = courtesyTitle;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }
    
    @Override
    public String toString() {
        return lastName + ", " + firstName;
    }
    
}
