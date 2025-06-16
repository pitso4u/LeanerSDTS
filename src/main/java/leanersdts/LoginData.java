/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

import java.util.Date;

/**
 *
 * @author pitso
 */
public class LoginData {
    private static LoginData instance;
    private int learnerId;
    private String username;
    private String password;
    private String fullName;
    private Date dateOfBirth;
    private String email;

    private LoginData() {}

    public static LoginData getInstance() {
        if (instance == null) {
            instance = new LoginData();
        }
        return instance;
    }

    public LoginData(int learnerId, String username, String password, String fullName, Date dateOfBirth, String email) {
        this.learnerId = learnerId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public int getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(int learnerId) {
        this.learnerId = learnerId;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
