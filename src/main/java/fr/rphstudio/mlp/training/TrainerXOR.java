package fr.rphstudio.mlp.training;

import java.util.InputMismatchException;

public class TrainerXOR implements ITraining {

    // Data set values
    private final static double[][] DATASET = {
            {-1,-1}, // 0 0  -> 0
            { 1,-1}, // 1 0  -> 1
            {-1, 1}, // 0 1  -> 1
            { 1, 1}, // 1 1  -> 1
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
        return 0.001;
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
        return 2;
    }

    @Override
    public int getOutputSize() {
        return 1;
    }

    @Override
    public int getNbDataSet() {
        return 4;
    }

    @Override
    public double[] getInputDataSet(int num) {
        if(num < 0 || num >= TrainerXOR.DATASET.length ){
            throw new InputMismatchException("The number of the input data set is not correct : "+num);
        }
        return TrainerXOR.DATASET[num];
    }

    @Override
    public double[] getOutputDataSet(int num) {
        if(num < 0 || num >= TrainerXOR.DATASET.length ){
            throw new InputMismatchException("The number of the input data set is not correct : "+num);
        }
        double[] out = { -1 };
        if(num == 1 || num == 2){
            out[0] = 1;
        }
        return out;
    }

    @Override
    public String[] getInputLabels() {
        String[] out = {"a", "b"};
        return out;
    }

    @Override
    public String[] getOutputLabels() {
        String[] out = {"out : a^b"};
        return out;
    }

}
