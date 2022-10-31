package manager;

import agent.impl.DecryptionWorkerImpl;
import dto.DictionaryLoadInfo;
import generated.CTEEnigma;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import service.XmlFileLoader;
import service.XmlSchemaVerifier;
import service.impl.XmlSchemaVerifierImpl;
import generated.CTEDecipher;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DictionaryManagerStatic {
    private static final Logger log = Logger.getLogger(DictionaryManagerStatic.class);
    static {
        try {
            Properties p = new Properties();
            p.load(DictionaryManagerStatic.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + DictionaryManagerStatic.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + DictionaryManagerStatic.class.getSimpleName() ) ;
        }
    }


    @Getter @Setter private static String Abc;
    @Getter private static Map<String,String> dictionary = new HashMap<>();
    @Getter private static SimpleObjectProperty<List<String>> dictionaryProperty = new SimpleObjectProperty<>();
    @Getter private static List<String> excludeChars = new ArrayList<>();
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

    public static void loadDictionary(DictionaryLoadInfo loadInfo){
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

    public static String cleanWord(String word) {
        for (int i = 0; i < word.length(); i++) {
            for (String excludedChar : excludeChars) {
                word = word.trim().replace(excludedChar, EMPTY_CHAR).toUpperCase();
            }
        }
        return word;
    }

    public static void loadDictionary(InputStream inputStream) throws Exception {
        XmlSchemaVerifier xmlSchemaVerifier = new XmlSchemaVerifierImpl();
        CTEEnigma cteEnigma = XmlFileLoader.fromXmlFileToCTE(inputStream);
        if(cteEnigma == null){
            throw new Exception("Failed to generate JAXB CTE Enigma objects by schema");
        }
        buildDictionary(cteEnigma);
    }

    public static boolean isInDictionary(String word){
        if(word == null){
            return false;
        }
        String cleanedWord = cleanWord(word);
        String capitalCaseWord = "_"; //random string assumed not to be in any dictionary
        try{
            capitalCaseWord = cleanedWord.substring(0,1).toUpperCase() + cleanedWord.substring(1,cleanedWord.length()).toLowerCase();
        }
        catch (Exception e){
            log.error("Failed to create capitalCaseWord for the word=" + word);

        }
        return getDictionary().containsKey(cleanedWord.toLowerCase()) || getDictionary().containsKey(cleanedWord.toUpperCase()) || getDictionary().containsKey(capitalCaseWord);
    }

    public static DictionaryLoadInfo getLoadInfo(){
        DictionaryLoadInfo loadInfo = new DictionaryLoadInfo();
        if(loadInfo == null || loadInfo.getAbc() == null || loadInfo.getExcludedChars() == null || loadInfo.getWords() == null){
            return null;
        }
        loadInfo.setWords(DictionaryManagerStatic.getDictionary().keySet());
        loadInfo.setAbc(DictionaryManagerStatic.getAbc());
        loadInfo.setExcludedChars(new HashSet<>(DictionaryManagerStatic.getExcludeChars()));
        return loadInfo;
    }
}
