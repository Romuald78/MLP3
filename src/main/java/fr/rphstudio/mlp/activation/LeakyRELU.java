package fr.rphstudio.mlp.activation;


public class LeakyRELU implements ActivationFunction {

    @Override
    public double function(double x, double[] allX) {
        if (x<0){
            x *= 0.05;
        }
        return x;
    }

    @Override
    public double derivative(double x, double[] allX) {
        double res = 1;
        if(x < 0){
            res *= 0.05;
        }
        return res;
    }

}
