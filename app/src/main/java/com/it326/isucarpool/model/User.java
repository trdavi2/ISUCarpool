package com.it326.isucarpool.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tim on 10/20/16.
 */

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String emergencyContactEmail;
    private boolean admin;
    private ArrayList<Chat> chatsList;
    private ArrayList<CarpoolOffer> carpoolOffers;
    //Whatever other attributes the User should have

    public User(){
        setAdmin(false);
        setFirstName("");
        setLastName("");
        setEmail("");
        setGender("");
        setAddress("");
        setCity("");
        setState("");
        setChatsList(new ArrayList<Chat>());
        setCarpoolOffers(new ArrayList<CarpoolOffer>());
    }

    public User(String firstName, String lastName, String email, String gender, String address, String city, String state, boolean admin)
    {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setGender(gender);
        this.setAddress(address);
        this.setCity(city);
        this.setState(state);
        this.setChatsList(new ArrayList<Chat>());
        this.setCarpoolOffers( new ArrayList<CarpoolOffer>());
        this.setAdmin(admin);

    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Chat> getChatsList() {
        return chatsList;
    }

    public void setChatsList(ArrayList<Chat> chatsList) {
        this.chatsList = chatsList;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public ArrayList<CarpoolOffer> getCarpoolOffers() { return carpoolOffers; }

    public void setCarpoolOffers(ArrayList<CarpoolOffer> carpoolOffers) { this.carpoolOffers = carpoolOffers; }

    public boolean getAdmin() { return admin; }

    public void setAdmin(boolean admin) { this.admin = admin; }

    public String getEmergencyContactEmail() { return emergencyContactEmail; }

    public void setEmergencyContactEmail(String emergencyContactEmail) { this.emergencyContactEmail = emergencyContactEmail; }
}
