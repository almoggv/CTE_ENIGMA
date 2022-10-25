package utils;

import java.util.Collection;

public class StringUtils {

    /**
     * Concatinates the collection of strings with the seperator between each string
     * @param collection if null returns null
     * @param seperator default value = ""
     * @return
     */
    public static String join(Collection<String> collection, String seperator){
        String resultString = "";
        boolean firstElemnt = true;
        if(collection == null || collection.isEmpty()){
            return null;
        }
        if(seperator == null){
            seperator = "";
        }
        for (String value : collection ) {
            if(!firstElemnt){
                resultString += seperator;
            }
            resultString += value;
            firstElemnt = false;
        }
        return resultString;
    }
}
