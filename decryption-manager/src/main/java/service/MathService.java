package service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MathService {

    //for permutation
    private static List<Integer> perms;
    private static int[] indexPerms;
    private static int[] directions;
    private static int[] iSwap;
    private static int N; //permute 0..N-1
    private static int movingPerm;

    static int FORWARD=+1;
    static int BACKWARD=-1;

    public static int factorial(int n){
        if(n < 0){
            throw new ArithmeticException("Operation on negative not supported");
        }
        int result = 1;
        for (int i= 1 ; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static int nChooseKSize(int k, int n) {
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

    public static List<int[]> nChooseKOptions(int n, int r) {
        List<int[]> combinations = new ArrayList<>();
        int[] combination = new int[r];

        // initialize with lowest lexicographic combination
        for (int i = 0; i < r; i++) {
            combination[i] = i;
        }

        while (combination[r - 1] < n) {
            combinations.add(combination.clone());

            // generate next combination in lexicographic order
            int t = r - 1;
            while (t != 0 && combination[t] == n - r + t) {
                t--;
            }
            combination[t]++;
            for (int i = t + 1; i < r; i++) {
                combination[i] = combination[i - 1] + 1;
            }
        }

        return combinations;
    }

    private static void createPermutations(int N) {
        MathService.N = N;
        perms = new ArrayList<>(N);     // permutations
        indexPerms = new int[N];     // index to where each permutation value 0..N-1 is
        directions = new int[N];     // direction = forward(+1) or backward (-1)
        iSwap = new int[N]; //number of swaps we make for each integer at each level
        for (int i = 0; i < N; i++) {
            directions[i] = BACKWARD;
//            perms[i]  = i;
            perms.add(i,i);
            indexPerms[i] = i;
            iSwap[i] = i;
        }
        movingPerm = N;
    }
    private static List<Integer> getNextPermutation() {
        //each call returns the next permutation
        do{
            if (movingPerm == N) {
                movingPerm--;
                return perms;
            } else if (iSwap[movingPerm] > 0) {
                //swap
                int swapPerm = perms.get(indexPerms[movingPerm] + directions[movingPerm]);
                perms.set(indexPerms[movingPerm],swapPerm);
                perms.set(indexPerms[movingPerm] + directions[movingPerm],movingPerm);
                indexPerms[swapPerm] = indexPerms[movingPerm];
                indexPerms[movingPerm] = indexPerms[movingPerm] + directions[movingPerm];
                iSwap[movingPerm]--;
                movingPerm=N;
            } else {
                iSwap[movingPerm] = movingPerm;
                directions[movingPerm] = -directions[movingPerm];
                movingPerm--;
            }
        } while (movingPerm > 0);
        return null;
    }

    public static List<List<Integer>> createPermutationList(int N) {
        MathService.createPermutations(N);
        List<List<Integer>> permutations =new ArrayList<>();
        List<Integer> next = MathService.getNextPermutation();
        while (next != null) {
            next = new ArrayList<>(next);
            permutations.add(next);
            next = MathService.getNextPermutation();
        }
        return permutations;
    }

    /**
     * i.e. abc=ABCDEF -> number=AAD , base=6 -> result=003
     * @param numberInBaseN - decomposed number
     * @param base - 10 (decimal) , 16 (hex)..
     * @param baseAbc - the abc of the base, i.e. 0-9 (decimal), 0-9a-f (hex)
     * @return
     */
    public static int fromLettersToBase10(List<String> numberInBaseN, int base, String baseAbc){
        int result = 0;
        for (int i = numberInBaseN.size() - 1, pow = 0 ; i >= 0 ; i--, pow++) {
            int charAsBase10 = baseAbc.indexOf(numberInBaseN.get(i));
            result += charAsBase10 * (Math.pow(base,pow));
        }
        return (int) result;
    }

    //

    /**
     * From base 10 to baseN i.e. BaseN = 26 (A-Z) -> 29 -> ABD
     * @param decimalNumber - actual value
     * @param baseN
     * @param abc the abc of the BaseN
     * @param numberOfLettersInUse the number of characters in the result (numberOfRotors In Use)
     * @return
     */
    public static List<String> fromBase10ToBaseN(int decimalNumber, int baseN, String abc, int numberOfLettersInUse){
        LinkedList<String> result = new LinkedList<>();
        for (int i = 0; i < numberOfLettersInUse; i++) {
            int currDigit = decimalNumber % baseN;
            decimalNumber = decimalNumber / baseN;
            result.push(abc.substring(currDigit,currDigit+1));
        }
        return result;
    }


}
