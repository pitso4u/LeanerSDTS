/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.leanersdts;

import java.time.LocalDate;

/**
 *
 * @author pitso
 */
public class LoginData {
    private int learner_id;
   private String username;
    private String password;
    private String full_name;
   private LocalDate date_of_birth;
    private String email;

    public LoginData() {
    }

    public LoginData(int learner_id, String username, String password, String full_name, LocalDate date_of_birth, String email) {
        this.learner_id = learner_id;
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.date_of_birth = date_of_birth;
        this.email = email;
    }

    public int getLearner_id() {
        return learner_id;
    }

    public void setLearner_id(int learner_id) {
        this.learner_id = learner_id;
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

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDate date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
