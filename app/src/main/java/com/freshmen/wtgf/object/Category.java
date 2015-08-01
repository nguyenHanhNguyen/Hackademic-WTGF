package com.freshmen.wtgf.object;

/**
 * Created by Hung on 01/08/2015.
 */
public class Category {
    private String name;
    private String description;


    public Category() {
    }

    public Category(String name, String desc) {
        this.name = name;
        this.description = desc;
    }

    public Category(String desc) {
        this.description = desc;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
