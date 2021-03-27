package com.blackseapps.heskodkontrol.Modal;

public class Login {

    String tc;
    String password;
    String username;

    public Login(String tc, String password, String username) {
        this.tc = tc;
        this.password = password;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
