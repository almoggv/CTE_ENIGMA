package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionInfoHistory implements Serializable {
    private String input;
    private String output;
    private long timeToEncrypt;
}
