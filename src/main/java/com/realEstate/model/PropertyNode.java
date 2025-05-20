package com.realEstate.model;

public class PropertyNode {
    public Property property;
    public PropertyNode left, right;

    public PropertyNode(Property property) {
        this.property = property;
        left = right = null;
    }
}
