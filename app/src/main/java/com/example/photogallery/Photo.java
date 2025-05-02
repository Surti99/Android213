package com.example.photogallery;

import java.util.ArrayList;
import java.util.List;

public class Photo {
    private String path;
    private List<Tag> tags;

    public Photo(String path) {
        this.path = path;
        this.tags = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }
} 