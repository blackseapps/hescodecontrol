package com.blackseapps.heskodkontrol.Modal;

public class ResponseStatus {

    String tc;
    String name;
    String hescode;
    String status;
    String date;

    public ResponseStatus(String tc, String name, String hescode, String status, String date) {
        this.tc = tc;
        this.name = name;
        this.hescode = hescode;
        this.status = status;
        this.date = date;
    }

    public String getTc() {
        return tc;
    }

    public String getName() {
        return name;
    }

    public String getHescode() {
        return hescode;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}
