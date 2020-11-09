package com.example.demo.Services;

import java.util.List;
import java.util.regex.*;

public class userPreference {
    private List<String> q1;
    private String q2;
    private List<String> q3;

    public userPreference(List<String> q1, String q2, List<String> q3){
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
    };
    public userPreference(userPreference input_userPreference){
        this.q1 = input_userPreference.q1;
        this.q2 = input_userPreference.q2;
        this.q3 = input_userPreference.q3;
    }

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


    public boolean equals(userPreference pref) {
        if(this.q1.equals(pref.q1)&&this.q2.equals(pref.q2)&&this.q3.equals(pref.q3))
            return true;
        else if(pref==null)
            return false;
        else if((pref.q1==null)||(pref.q2==null) || (pref.q3==null))
            return false;
        return false;
//        return super.equals(obj);
    }
}
