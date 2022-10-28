package service;

import org.junit.Test;

import java.util.List;

public class MathServiceTest {

    @Test
    public void nChooseKOpstionsTest(){
        int n = 3;
        int k = 3;
        List<int[]> result = MathService.nChooseKOptions(n,k);
        System.out.println(result);
    }
}
