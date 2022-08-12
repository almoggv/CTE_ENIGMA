package main.java.generictype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MappingPairListUtils <T1,T2>{

    public static <T1,T2> T1 getLeftByRight(List<MappingPair<T1,T2>> sourceCollection, T2 right) throws NullPointerException {
        MappingPair<T1,T2> foundPair = findPairByRight(sourceCollection,right);
        return (foundPair != null) ? foundPair.getLeft() : null;
    }

    public static <T1,T2> MappingPair<T1,T2> findPairByRight(List<MappingPair<T1,T2>> sourceCollection, T2 right) throws NullPointerException {
        if(sourceCollection == null){
            throw new NullPointerException();
        }
        List<T1> leftList = new ArrayList<>(sourceCollection.size());
        List<T2> rightList = new ArrayList<>(sourceCollection.size());
        initLeftAndRightCollections(sourceCollection,leftList,rightList);
        int index = rightList.indexOf(right);
        if(index < 0){
            //TODO: log here
            return null;
        }
        return sourceCollection.get(index);
    }

    public static <T1 ,T2> T2 getRightByLeft(List<MappingPair<T1,T2>> sourceCollection, T1 left) throws NullPointerException {
        MappingPair<T1,T2> foundPair = findPairByLeft(sourceCollection,left);
        return (foundPair != null) ? foundPair.getRight() : null;
    }

    public static <T1,T2> MappingPair<T1,T2> findPairByLeft(List<MappingPair<T1,T2>> sourceCollection, T1 left) throws NullPointerException{
        if(sourceCollection == null){
            throw new NullPointerException("Cannot find Pair by left, List is null");
        }
        List<T1> leftList = new ArrayList<>(sourceCollection.size());
        List<T2> rightList = new ArrayList<>(sourceCollection.size());
        initLeftAndRightCollections(sourceCollection,leftList,rightList);
        int index = leftList.indexOf(left);
        if(index < 0){
            return null;
        }
        return sourceCollection.get(index);
    }

    private static <T1,T2> void initLeftAndRightCollections(Collection<MappingPair<T1,T2>> sourceCollection, Collection<T1> leftList, Collection<T2> rightList){
        for ( MappingPair<T1,T2> mapPair : sourceCollection ) {
            leftList.add(mapPair.getLeft());
            rightList.add(mapPair.getRight());
        }
        return;
    }

    public static <T1 extends Comparable,T2 extends  Comparable> List<MappingPair<T1,T2>> sortByLeft(List<MappingPair<T1,T2>> sourceList){
        return sourceList.stream().sorted(((o1, o2) -> o1.getLeft().compareTo(o2.getLeft()))).
                collect(Collectors.toList());
    }
}
