package com.example.demo.Services;

import java.util.List;
import java.util.regex.*;

public class userPreference {
    private List<String> q1;
    private String q2;
    private List<String> q3;

    public userPreference(){};

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

    public String getQ2() { return q2;}

    public List<String> getQ3() {
        return q3;
    }

    public double getTimefromQ2(){
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(q2);
        double res = -1;
        while(m.find()) {
            res = Double.parseDouble(m.group());
        }
        return res;
    }
}
