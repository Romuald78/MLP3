package fr.rphstudio.mlp.activation;


public class TanH implements ActivationFunction {

    @Override
    public double function(double x, double[] allX) {
        return Math.tanh(x);
    }


    @Override
    public double derivative(double x, double[] allX) {
        double y = this.function(x,allX);
        return 1-(y*y);
    }


}
