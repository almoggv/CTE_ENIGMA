package test.java;

import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import service.MathService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestMathService {

//    @Test public void permutation(){
//        MathService.createPermutations(3);
//        List<List<Integer>> permutations =new ArrayList<>();
//        List<Integer> next = MathService.getNextPermutation();
//        while (next != null) {
//            next = new ArrayList<>(next);
//            permutations.add(next);
//            next = MathService.getNextPermutation();
//        }
//        System.out.println(permutations);
//    }

    @Test public void permOfGivenId() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic-easy.xml";

        machineHandler.buildMachinePartsInventory(path);

        machineHandler.assembleMachine();

        List<Integer> rotorsInUse = machineHandler.getMachineState().get().getRotorIds();
        List<List<Integer>> indexesPermutationsList = MathService.createPermutationList(rotorsInUse.size());

        List<List<Integer>> allRotorIdsPlacement = new ArrayList<>();

        for (List<Integer> indexList : indexesPermutationsList) {
            List<Integer> singleRotorIdList = new ArrayList<>();
            for (Integer index : indexList) {
                singleRotorIdList.add(rotorsInUse.get(index));
            }
            allRotorIdsPlacement.add(singleRotorIdList);
        }
        System.out.println(allRotorIdsPlacement);
    }

    @Test public void nCk() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic-easy.xml";

        machineHandler.buildMachinePartsInventory(path);
        machineHandler.assembleMachine();

        int nCk = MathService.nChooseKSize(machineHandler.getInventoryInfo().get().getNumOfRotorsInUse(),machineHandler.getInventoryInfo().get().getNumOfAvailableRotors() );
        System.out.println(nCk);
    }
    @Test public void nCk2() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic-easy.xml";

        machineHandler.buildMachinePartsInventory(path);
        machineHandler.assembleMachine();

        List<int[]> nck= MathService.nChooseKOptions(machineHandler.getInventoryInfo().get().getNumOfAvailableRotors(),machineHandler.getInventoryInfo().get().getNumOfRotorsInUse() );
//        List<int[]> nck= MathService.generate(2,3 );
//        List<int[]> nck= MathService.generate(3,2 );
        System.out.println(nck);
    }

    @Test public void nCk_get_ids(){
        List<int[]> indexesComboList = MathService.nChooseKOptions(3, 2);
        List<List<Integer>> allRotorIdsPlacement = new ArrayList<>();

        for (int[] indexList : indexesComboList) {
            List<Integer> singleRotorIdList = new ArrayList<>();
            for (Integer index : indexList) {
                singleRotorIdList.add(index+1);
            }
            allRotorIdsPlacement.add(singleRotorIdList);
        }
        System.out.println(allRotorIdsPlacement);
    }
}
