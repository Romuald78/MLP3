package fr.rphstudio.misc;

public class LCD7 {

    /*
            0
           ----
          |    |
      5   |    |   1
          |    |
           ---- 6
          |    |
      4   |    |   2
          |    |
           ----
            3
     */

    private final static double[][] DIGITS = {
        {1,1,1,1,1,1,0}, // 0
        {0,1,1,0,0,0,0}, // 1
        {1,1,0,1,1,0,1}, // 2
        {1,1,1,1,0,0,1}, // 3
        {0,1,1,0,0,1,1}, // 4
        {1,0,1,1,0,1,1}, // 5
        {1,0,1,1,1,1,1}, // 6
        {1,1,1,0,0,0,0}, // 7
        {1,1,1,1,1,1,1}, // 8
        {1,1,1,1,0,1,1}, // 9
    };

    public static double[] getDigitInput(int num){
        return LCD7.DIGITS[num];
    }


}
