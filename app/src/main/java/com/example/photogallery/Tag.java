package com.example.photogallery;

public class Tag {
    public enum Type {
        PERSON,
        LOCATION
    }

    private Type type;
    private String value;

    public Tag(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return type == tag.type && value.equalsIgnoreCase(tag.value);
    }

    @Override
    public int hashCode() {
        return (type.toString() + value.toLowerCase()).hashCode();
    }
} 