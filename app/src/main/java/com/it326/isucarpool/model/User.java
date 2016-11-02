package com.it326.isucarpool.model;

import java.util.ArrayList;

/**
 * Created by Tim on 10/20/16.
 */

public class User
{
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String address;
    private String city;
    private ArrayList<Chat> chatsList;
    //Whatever other attributes the User should have

    public User(){
        firstName = "";
        lastName = "";
        email = "";
        gender = "";
    }

    public User(String firstName, String lastName, String email)
    {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setChatsList(new ArrayList<Chat>());
        this.getChatsList().add(new Chat());
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
}
