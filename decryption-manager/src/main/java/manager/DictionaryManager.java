package manager;

import dto.DictionaryLoadInfo;
import generated.CTEDecipher;
import generated.CTEEnigma;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import service.XmlFileLoader;
import service.XmlSchemaVerifier;
import service.impl.XmlSchemaVerifierImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryManager {
    @Getter
    @Setter
    private String Abc;
    @Getter private Map<String,String> dictionary = new HashMap<>();
    @Getter private SimpleObjectProperty<List<String>> dictionaryProperty = new SimpleObjectProperty<>();
    @Getter private List<String> excludeChars = new ArrayList<>();
    @Getter private final String DELIMITER = " ";
    private final String EMPTY_CHAR = "";

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

    public void loadDictionary(DictionaryLoadInfo loadInfo){
        if(loadInfo == null || loadInfo.getAbc() == null || loadInfo.getExcludedChars() == null || loadInfo.getWords() == null){
            return;
        }
        for (String word : loadInfo.getWords() ) {
            dictionary.putIfAbsent(word,word);
        }
        for (String excludedChar : loadInfo.getExcludedChars() ) {
            excludeChars.add(excludedChar);
        }
        Abc = loadInfo.getAbc();
    }

    private void buildDictionary(CTEEnigma cteEnigma) {
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

    private boolean inAbc(String cleanedWord) {
        for (int i = 0; i < cleanedWord.length(); i++) {
            if(!Abc.contains(cleanedWord.substring(i,i+1))){
                return false;
            }
        }
        return true;
    }

    private String cleanWord(String word) {
        for (int i = 0; i < word.length(); i++) {
            for (String excludedChar : excludeChars) {
                word = word.trim().replace(excludedChar, EMPTY_CHAR).toUpperCase();
            }
        }
        return word;
    }

    public void loadDictionary(InputStream inputStream) throws Exception {
        XmlSchemaVerifier xmlSchemaVerifier = new XmlSchemaVerifierImpl();
        CTEEnigma cteEnigma = XmlFileLoader.fromXmlFileToCTE(inputStream);
        if(cteEnigma == null){
            throw new Exception("Loading Dictionary - Failed to generate JAXB CTE Enigma objects by schema");
        }
        buildDictionary(cteEnigma);
    }

    public boolean isInDictionary(String word){
        if(word == null){
            return false;
        }
        String cleanedWord = cleanWord(word);
        String capitalCaseWord = cleanedWord.substring(0,1).toUpperCase() + cleanedWord.substring(1,cleanedWord.length()).toLowerCase();
        return getDictionary().containsKey(cleanedWord.toLowerCase()) || getDictionary().containsKey(cleanedWord.toUpperCase()) || getDictionary().containsKey(capitalCaseWord);
    }

}
