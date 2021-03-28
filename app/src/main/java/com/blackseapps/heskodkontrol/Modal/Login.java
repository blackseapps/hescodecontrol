package com.blackseapps.heskodkontrol.Modal;

public class Login {

    String tc;
    String password;
    String username;
    String last;

    public Login(String tc, String password, String username, String last) {
        this.tc = tc;
        this.password = password;
        this.username = username;
        this.last = last;
    }

    public String getLast() {
        return last;
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
