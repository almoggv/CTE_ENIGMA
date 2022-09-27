package main.java.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionDto {
    int mainVersion;
    int subVersion;

    @Override
    public String toString(){
        return "" + mainVersion + "." + subVersion;
    }
}
