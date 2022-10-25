package com.heb.hebcodingchallenge.models.json;

import java.util.ArrayList;
import java.util.List;
public class Result {
    private List<Tag> tags = new ArrayList<Tag>();
    public List<Tag> getTags() {
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}