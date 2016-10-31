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
    private ArrayList<Chat> chatsList;
    //Whatever other attributes the User should have

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

}
