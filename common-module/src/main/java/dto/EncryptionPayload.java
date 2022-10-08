package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptionPayload {
    private String message;
    private EncryptionInfoHistory encryptionInfoHistory;
}
