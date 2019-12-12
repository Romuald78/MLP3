package fr.rphstudio.mlp.training;

public interface ITraining {

    // Get learning rate range
    public double getMinLearningRate();
    public double getMaxLearningRate();

    // Get error limit
    public double getAllowedError();

    // Get count of dataset below / above the error limit
    public int getNbMaxCorrectDataSet();
    public int getNbMaxBadDataSet();

    // Get size of the input / output data
    public int getInputSize();
    public int getOutputSize();

    // Get data set information (number / in / out)
    public int getNbDataSet();
    public double[] getInputDataSet(int num);
    public double[] getOutputDataSet(int num);



}
