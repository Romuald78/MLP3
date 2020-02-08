package fr.rphstudio.mlp.training;

import java.util.InputMismatchException;

public class TrainerLCD7 implements ITraining {

    // Digit values
    private final static double[][] DATASET = {
            { 1, 1, 1, 1, 1, 1,-1}, // 0
            {-1, 1, 1,-1,-1,-1,-1}, // 1
            { 1, 1,-1, 1, 1,-1, 1}, // 2
            { 1, 1, 1, 1,-1,-1, 1}, // 3
            {-1, 1, 1,-1,-1, 1, 1}, // 4
            { 1,-1, 1, 1,-1, 1, 1}, // 5
            { 1,-1, 1, 1, 1, 1, 1}, // 6
            { 1, 1, 1,-1,-1,-1,-1}, // 7
            { 1, 1, 1, 1, 1, 1, 1}, // 8
            { 1, 1, 1, 1,-1, 1, 1}, // 9
            { 1, 1, 1,-1, 1, 1, 1}, // A
            {-1,-1, 1, 1, 1, 1, 1}, // B
            { 1,-1,-1, 1, 1, 1,-1}, // C
            {-1, 1, 1, 1, 1,-1, 1}, // D
            { 1,-1,-1, 1, 1, 1, 1}, // E
            { 1,-1,-1,-1, 1, 1, 1}, // E
    };

    @Override
    public double getMinLearningRate() {
        return 0.1;
    }

    @Override
    public double getMaxLearningRate() {
        return 0.5;
    }

    @Override
    public double getAllowedError() {
        return 0.01;
    }

    @Override
    public int getNbMaxCorrectDataSet() {
        return 100;
    }

    @Override
    public int getNbMaxBadDataSet() {
        return 10000000;
    }

    @Override
    public int getInputSize() {
        return TrainerLCD7.DATASET[0].length;
    }

    @Override
    public int getOutputSize() {
        return TrainerLCD7.DATASET.length;
    }

    @Override
    public int getNbDataSet() {
        return 16;
    }

    @Override
    public double[] getInputDataSet(int num) {
        if(num < 0 || num >= TrainerLCD7.DATASET.length ){
            throw new InputMismatchException("The number of the input data set is not correct : "+num);
        }
        return TrainerLCD7.DATASET[num];
    }

    @Override
    public double[] getOutputDataSet(int num) {
        if(num < 0 || num >= TrainerLCD7.DATASET.length ){
            throw new InputMismatchException("The number of the output data set is not correct : "+num);
        }
        double[] out = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        out[num] = 1;
        return out;
    }

    @Override
    public String[] getInputLabels() {
        String[] out = { "LCD0", "LCD1", "LCD2", "LCD3", "LCD4", "LCD5", "LCD6" };
        return out;
    }


    @Override
    public String[] getOutputLabels() {
        String[] outLabels = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        return outLabels;
    }

}
