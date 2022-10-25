package com.heb.hebcodingchallenge.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Image {

    private @Id @GeneratedValue Long id;
    private String label;

    private boolean enableObjectDetection=false;

    private String sourceImageLocation;

    private String fileName;

    private String sha256hex;

    public String getSourceImageLocation() {
        return sourceImageLocation;
    }

    @ManyToMany(fetch=FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<ImageObject> imageObjects = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnableObjectDetection() {
        return enableObjectDetection;
    }

    public void setEnableObjectDetection(boolean enableObjectDetection) {
        this.enableObjectDetection = enableObjectDetection;
    }

    public Set<ImageObject> getImageObjects() {
        return imageObjects;
    }

    public void setImageObjects(Set<ImageObject> imageObjects) {
        this.imageObjects = imageObjects;
    }

    public void setSourceImageLocation(String imageLocation) {
        this.sourceImageLocation = imageLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSha256hex() {
        return sha256hex;
    }

    public void setSha256hex(String sha256hex) {
        this.sha256hex = sha256hex;
    }

}
