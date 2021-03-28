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

    public String getTc() {
        return tc;
    }

    public String getPassword() {
        return password;
    }

}
