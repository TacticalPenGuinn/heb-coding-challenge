package com.heb.hebcodingchallenge.models;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class ImageObject {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @NaturalId
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
