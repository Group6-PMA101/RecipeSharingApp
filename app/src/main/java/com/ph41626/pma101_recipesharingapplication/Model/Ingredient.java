package com.ph41626.pma101_recipesharingapplication.Model;

public class Ingredient {
    private String id;
    private String name;
    private float mass;

    public Ingredient() {
    }

    public Ingredient(String id, String name, float mass) {
        this.id = id;
        this.name = name;
        this.mass = mass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mass=" + mass +
                '}';
    }
}
