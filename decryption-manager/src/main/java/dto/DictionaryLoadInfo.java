package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryLoadInfo {
    Set<String> words;
    String abc;
    Set<String> excludedChars;
}
