package decryptionLevel;

import enums.DecryptionDifficultyLevel;
import org.junit.Assert;
import org.junit.Test;


public class TestDecryptionLevel {
    @Test
    public void checkGetLevelByName(){
        String levelName = "Easy";
        DecryptionDifficultyLevel level = DecryptionDifficultyLevel.getByName(levelName);
        System.out.println(level);
    }
}
