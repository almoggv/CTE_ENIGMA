package main.java.manager;

import lombok.Getter;
import lombok.Setter;
import main.java.service.XmlFileLoader;
import main.java.service.XmlSchemaVerifier;
import main.java.service.impl.XmlSchemaVerifierImpl;
import main.resources.generated.CTEDecipher;
import main.resources.generated.CTEEnigma;

import java.util.*;

public class DictionaryManager {
    @Getter @Setter private static String Abc;
    @Getter private static final Map<String,String> dictionary = new HashMap<>();
    private static List<String> excludeChars = new ArrayList<>();

    @Getter private static final String DELIMITER = " ";
    private static final String EMPTY_CHAR = "";
    public void loadDictionary(String absolutePath) throws Exception {
        XmlSchemaVerifier xmlSchemaVerifier = new XmlSchemaVerifierImpl();
        try{
            xmlSchemaVerifier.isFileInExistenceAndXML(absolutePath);
        }
        catch (Exception e){

        }
        CTEEnigma cteEnigma = XmlFileLoader.fromXmlFileToCTE(absolutePath);
        if(cteEnigma == null){
            throw new Exception("Failed to generate JAXB CTE Enigma objects by schema");
        }
        buildDictionary(cteEnigma);
    }

    private void buildDictionary(CTEEnigma cteEnigma) {
        CTEDecipher cteDecipher = cteEnigma.getCTEDecipher();
        String dictionaryWordsString = cteDecipher.getCTEDictionary().getWords();
        String excludeCharsString = cteDecipher.getCTEDictionary().getExcludeChars();
        excludeChars = new ArrayList<>();
        for (int i = 0; i < excludeCharsString.length(); i++) {
            excludeChars.add(excludeCharsString.substring(i,i+1));
        }

        String[] dictionaryWords = dictionaryWordsString.split(DELIMITER);
        for (String word : dictionaryWords) {
            String cleanedWord = cleanWord(word);
            dictionary.putIfAbsent(cleanedWord,cleanedWord);
        }
    }

    private String cleanWord(String word) {
        for (int i = 0; i < word.length(); i++) {
            for (String excludedChar : excludeChars) {
                word = word.replace(excludedChar, EMPTY_CHAR);
            }
        }
        return word;
    }
}
