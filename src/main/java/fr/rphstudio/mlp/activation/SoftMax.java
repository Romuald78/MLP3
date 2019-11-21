package fr.rphstudio.mlp.activation;


public class SoftMax implements ActivationFunction {

    @Override
    public double function(double x, double[] allX) {
        // Get sum of all X
        double sum = 0;
        for(int i=0;i<allX.length;i++){
            sum += Math.exp( allX[i] );
        }
        return Math.exp(x)/sum;
    }

    @Override
    public double derivative(double x, double[] allX) {
        double y = this.function(x,allX);
        return y*(1-y);
    }

}
