package main.java.service;

import java.util.ArrayList;
import java.util.List;

public class MathService {

    public static int factorial(int n){
        if(n < 0){
            throw new ArithmeticException("Operation on negative not supported");
        }
        int result = 1;
        for (int i = 0; i < n; i++) {
            result *= i;
        }
        return result;
    }

    public static int nChooseK(int k, int n) {
        int C[][] = new int[n + 1][k + 1];
        int i, j;

        // Calculate  value of Binomial
        // Coefficient in bottom up manner
        for (i = 0; i <= n; i++) {
            for (j = 0; j <= Math.min(i, k); j++) {
                // Base Cases
                if (j == 0 || j == i)
                    C[i][j] = 1;

                    // Calculate value using
                    // previously stored values
                else
                    C[i][j] = C[i - 1][j - 1] + C[i - 1][j];
            }
        }

        return C[n][k];
    }
}
