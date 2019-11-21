package fr.rphstudio.mlp.activation;


public class Sigmoid implements ActivationFunction {

    @Override
    public double function(double x, double[] allX) {
        return 1.0/(1.0+Math.exp(-x));
    }


    @Override
    public double derivative(double x, double[] allX) {
        double y = this.function(x,allX);
        return y*(1*y);
    }


}
