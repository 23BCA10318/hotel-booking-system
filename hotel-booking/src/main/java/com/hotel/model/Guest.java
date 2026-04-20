package com.hotel.model;

public class Guest {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String idType;
    private String idNumber;

    public Guest() {}

    public Guest(int id, String firstName, String lastName, String email,
                 String phone, String address, String idType, String idNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.idType = idType;
        this.idNumber = idNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
}
