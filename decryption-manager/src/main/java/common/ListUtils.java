package common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListUtils {

    /**
     * @param listTypeFlag True = get result as LinkedList, Flase = get result as ArrayList
     * @return
     */
    public static <T> List<List<T>> partition(List<T> sourceList, int partitionSize, boolean listTypeFlag){
        List<List<T>> resultPartitions;
        if(listTypeFlag == false){
            resultPartitions = new ArrayList<>();
        }
        else{
            resultPartitions = new LinkedList<>();
        }
        for (int i=0; i<sourceList.size(); i += partitionSize) {
            resultPartitions.add(new ArrayList<>(sourceList.subList(i, Math.min(i + partitionSize, sourceList.size()))));
        }

        return resultPartitions;
    }


}
