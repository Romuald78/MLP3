package fr.rphstudio.mlp.activation;

import java.io.Serializable;

public interface ActivationFunction extends Serializable {

    public double function  (double x, double[] allX);
    public double derivative(double x, double[] allX);


}
