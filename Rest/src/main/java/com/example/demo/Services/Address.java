package com.example.demo.Services;

public class Address {
    private String start_bound;
    private String end_bound;

    public Address(String start_bound, String end_bound){
        this.start_bound =start_bound;
        this.end_bound =end_bound;
    };
    public void setStart_bound(String start_bound) {
        this.start_bound = start_bound;
    }

    public void setEnd_bound(String end_bound) {
        this.end_bound = end_bound;
    }

    public String getStart_bound() {
        return start_bound;
    }

    public String getEnd_bound() {
        return end_bound;
    }
}
