package com.sabrinaBio.application.Modal;

public enum AvisType {
    beforeAfter,
    comment;

    public static AvisType fromString(String typeStr) {
        for (AvisType type : AvisType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum type: " + typeStr);
    }
}
