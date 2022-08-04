package main.java.component.impl;

import lombok.*;


@Data
public class MappingPair <T1,T2> {
    private T1 left;
    private T2 right;

    public MappingPair() {
    }

    public MappingPair( T1 left, T2 right) {
        this.right = right;
        this.left = left;
    }
}
