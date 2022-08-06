package main.java.generictype;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingPair <T1,T2> {
    private T1 left;
    private T2 right;
}
