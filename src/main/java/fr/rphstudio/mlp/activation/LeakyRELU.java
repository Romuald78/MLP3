package fr.rphstudio.mlp.activation;


public class LeakyRELU implements ActivationFunction {

    @Override
    public double function(double x, double[] allX) {
        return Math.max( -0.1*x, x );
    }

    @Override
    public double derivative(double x, double[] allX) {
        double res = 1;
        if(x < 0){
            res = -0.1;
        }
        return res;
    }

}
