package common;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static <T> List<List<T>> partition(List<T> sourceList, int partitionSize){
        List<List<T>> resultPartitions = new ArrayList<>();

        for (int i=0; i<sourceList.size(); i += partitionSize) {
            resultPartitions.add(new ArrayList<>(sourceList.subList(i, Math.min(i + partitionSize, sourceList.size()))));
        }

        return resultPartitions;
    }
}
