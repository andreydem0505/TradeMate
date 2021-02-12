package com.dementev_a.trademate.json;

import org.jetbrains.annotations.NotNull;

public class EmployeeJson {
    private String name;
    private String email;

    public EmployeeJson() {}

    public EmployeeJson(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    @Override
    public String toString() {
        return "EmployeeJson{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
