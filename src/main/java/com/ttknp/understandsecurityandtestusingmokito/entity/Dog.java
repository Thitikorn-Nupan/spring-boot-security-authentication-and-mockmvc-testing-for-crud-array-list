package com.ttknp.understandsecurityandtestusingmokito.entity;

public class Dog {

    private Long id;
    private String name;
    private String type;
    private String gender;
    private Double price;

    public Dog(Long id, String name, String type, String gender, Double price) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.price = price;
    }

    public Dog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
