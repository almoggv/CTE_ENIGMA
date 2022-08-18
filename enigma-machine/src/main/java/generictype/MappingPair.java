package main.java.generictype;

import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingPair <T1,T2> implements Serializable {
    private T1 left;
    private T2 right;
}
