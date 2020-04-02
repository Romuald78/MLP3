package fr.rphstudio.mlp.training;

import java.util.InputMismatchException;

public class TrainerAutoEncoder implements ITraining {


    // Constructor : place the two cameras randomly
    public TrainerAutoEncoder(){
    }




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
        return 0.2;
    }

    @Override
    public int getNbMaxCorrectDataSet() {
        return 150;
    }

    @Override
    public int getNbMaxBadDataSet() {
        return 10000000;
    }

    @Override
    public int getInputSize() {
        return 8;
    }

    @Override
    public int getOutputSize() {
        return this.getInputSize();
    }

    @Override
    public int getNbDataSet() {
        return (int)(Math.pow(2,this.getInputSize()));
    }


    @Override
    public double[] getInputDataSet(int num) {
        // check input
        if(num < 0 || num >= this.getNbDataSet() ){
            throw new InputMismatchException("The number of the input data set is not correct : "+num);
        }
        // Create data structure
        double[] in = new double[this.getInputSize()];
        // Put bits into structure
        for(int i=0;i<this.getInputSize();i++){
            in[i] = ((num>>i)&0x00000001) == 1 ? 1.0 : -1.0;
        }
        // return input data
        return in;
    }


    @Override
    public double[] getOutputDataSet(int num) {
        if(num < 0 || num >= this.getNbDataSet() ){
            throw new InputMismatchException("The number of the output data set is not correct : "+num);
        }
        return this.getInputDataSet(num);
    }

    @Override
    public String[] getOutputLabels() {
        return null;
    }

    @Override
    public String[] getInputLabels() {
        return null;
    }


}
