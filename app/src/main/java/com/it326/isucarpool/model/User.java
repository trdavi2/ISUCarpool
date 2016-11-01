package com.it326.isucarpool.model;

/**
 * Created by Tim on 10/20/16.
 */

public class User
{
    private String firstName;
    private String lastName;
    private String email;
    //Whatever other attributes the User should have

    public User(){
        firstName = "";
        lastName = "";
        email = "";
    }

    public User(String firstName, String lastName, String email)
    {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
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

}
