package com.example.demo.Services;

import java.util.List;

public class userPreference {
    private List<String> q1;
    private String q2;
    private List<String> q3;

    public void setQ1(List<String> q1) {
        this.q1 = q1;
    }

    public void setQ2(String q2) {
        this.q2 = q2;
    }

    public void setQ3(List<String> q3) {
        this.q3 = q3;
    }

    public List<String> getQ1(){
        return q1;
    }

    public String getQ2() {
        return q2;
    }

    public List<String> getQ3() {
        return q3;
    }
}
