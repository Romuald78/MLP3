package fr.rphstudio.mlp.activation;


public class Sigmoid implements ActivationFunction {

    private double internal(double x, double[] allX) {
        return 1.0/(1.0+Math.exp(-x));
    }

    @Override
    public double function(double x, double[] allX) {
        double y = this.internal(x, allX);
        return 2*y - 1.0;
    }


    @Override
    public double derivative(double x, double[] allX) {
        double y = this.internal(x,allX);
        return 2*y*(1-y);
    }


}
