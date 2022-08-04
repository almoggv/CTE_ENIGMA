package main.java.component.impl;

import lombok.*;


@Data
public class MappingPair <T1,T2> {
    private T1 left;
    private T2 right;
}
