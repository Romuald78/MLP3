package fr.rphstudio.mlp.activation;

public interface ActivationFunction {

    public double function  (double x, double[] allX);
    public double derivative(double x, double[] allX);


}
