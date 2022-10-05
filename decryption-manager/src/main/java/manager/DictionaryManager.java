package manager;

import generated.CTEEnigma;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import service.XmlFileLoader;
import service.XmlSchemaVerifier;
import service.impl.XmlSchemaVerifierImpl;
import generated.CTEDecipher;
import generated.CTEEnigma;

import java.util.*;

public class DictionaryManager {
    @Getter @Setter private static String Abc;
    @Getter private static Map<String,String> dictionary = new HashMap<>();
    @Getter private static SimpleObjectProperty<List<String>> dictionaryProperty = new SimpleObjectProperty<>();
    private static List<String> excludeChars = new ArrayList<>();
    @Getter private static final String DELIMITER = " ";
    private static final String EMPTY_CHAR = "";
    public static void loadDictionary(String absolutePath) throws Exception {
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

    private static void buildDictionary(CTEEnigma cteEnigma) {
        dictionary = new HashMap<>();
        dictionaryProperty.setValue(null);

        CTEDecipher cteDecipher = cteEnigma.getCTEDecipher();
        Abc = cteEnigma.getCTEMachine().getABC();
        String dictionaryWordsString = cteDecipher.getCTEDictionary().getWords();
        String excludeCharsString = cteDecipher.getCTEDictionary().getExcludeChars();
        excludeChars = new ArrayList<>();
        for (int i = 0; i < excludeCharsString.length(); i++) {
            excludeChars.add(excludeCharsString.substring(i,i+1));
        }

        String[] dictionaryWords = dictionaryWordsString.split(DELIMITER);
        for (String word : dictionaryWords) {
            String cleanedWord = cleanWord(word);
            if(inAbc(cleanedWord)){
                dictionary.putIfAbsent(cleanedWord,cleanedWord);
            }
        }
        List<String> dictionaryList = new ArrayList<>();
        for (String word : dictionary.keySet()) {
            dictionaryList.add(word);
        }
        dictionaryProperty.setValue(dictionaryList);
    }

    private static boolean inAbc(String cleanedWord) {
        for (int i = 0; i < cleanedWord.length(); i++) {
            if(!Abc.contains(cleanedWord.substring(i,i+1))){
                return false;
            }
        }
        return true;
    }

    private static String cleanWord(String word) {
        for (int i = 0; i < word.length(); i++) {
            for (String excludedChar : excludeChars) {
                word = word.trim().replace(excludedChar, EMPTY_CHAR).toUpperCase();
            }
        }
        return word;
    }
}
