package com.example.photogallery;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public void movePhotoTo(Photo photo, Album targetAlbum) {
        if (photos.remove(photo)) {
            targetAlbum.addPhoto(photo);
        }
    }
} 