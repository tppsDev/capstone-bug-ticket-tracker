/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

import sdtracker.utility.PasswordUtil;

/**
 *
 * @author Tim Smith
 */
public class AppUser {
    private int id;
    private String firstName;
    private String lastName;
    private String midInitial;
    private String email;
    private String phone1;
    private String phone1Type;
    private String phone2;
    private String phone2Type;
    private String username;
    private String password;
    private String salt;
    private String jobTitle;
    private String courtesyTitle;
    private Department department;
    private AppUser manager;
    private SecurityRole securityRole;

    public AppUser() {
    }

    public AppUser(int id) {
        this.id = id;
    }

    public AppUser(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public AppUser(int id, String username, String password, String salt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
    }
    
    public AppUser(int id, String firstName, String lastName, String email, String phone1) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone1 = phone1;
    }
    
    public AppUser(int id, String firstName, String lastName, String midInitial, String email, 
            String phone1, String phone1Type, String phone2, String phone2Type, String username, String password, String salt,
            String jobTitle, String courtesyTitle, Department department, AppUser mgr, SecurityRole securityRole) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.midInitial = midInitial;
        this.email = email;
        this.phone1 = phone1;
        this.phone1Type = phone1Type;
        this.phone2 = phone2;
        this.phone2Type = phone2Type;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.jobTitle = jobTitle;
        this.courtesyTitle = courtesyTitle;
        this.department = department;
        this.manager = mgr;
        this.securityRole = securityRole;
    }
    
    /**
     * setEncryptedPassword accepts a password String and encrypts it and stores it as well as stores the salt hash.
     * @param newPassword
     */
    public void setEncryptedPassword(String newPassword) {
        salt = PasswordUtil.getSalt(30);
        password = PasswordUtil.generateSecurePassword(newPassword, salt);
    }
    
    /**
     * checkPassword accepts a password String to check against the current password for the AppUser object. It uses
     * PasswordUtil verifyPassword method to which uses the stored password and stored salt to verify the passed String.
     * 
     * @param enteredPassword
     * @return
     */
    public boolean checkPassword(String enteredPassword) {
        return PasswordUtil.verifyUserPassword(enteredPassword, password, salt);
    }
    
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return lastName + ", " + firstName;
    }

    // Getter and Setter Methods for class properties

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

    public String getMidInitial() {
        return midInitial;
    }

    public void setMidInitial(String midInitial) {
        this.midInitial = midInitial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone1Type() {
        return phone1Type;
    }

    public void setPhone1Type(String phone1Type) {
        this.phone1Type = phone1Type;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone2Type() {
        return phone2Type;
    }

    public void setPhone2Type(String phone2Type) {
        this.phone2Type = phone2Type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public AppUser getManager() {
        return manager;
    }

    public void setManager(AppUser manager) {
        this.manager = manager;
    }

    public SecurityRole getSecurityRole() {
        return securityRole;
    }

    public void setSecurityRole(SecurityRole securityRole) {
        this.securityRole = securityRole;
    }
    
}
