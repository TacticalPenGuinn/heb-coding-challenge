package com.heb.hebcodingchallenge.models.json;

public class Tag {
    private Double confidence;
    private Tag_ tag;
    public Double getConfidence() {
        return confidence;
    }
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    public Tag_ getTag() {
        return tag;
    }
    public void setTag(Tag_ tag) {
        this.tag = tag;
    }
}