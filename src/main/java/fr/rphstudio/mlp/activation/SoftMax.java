package fr.rphstudio.mlp.activation;


public class SoftMax implements ActivationFunction {

    @Override
    public double function(double x, double[] allX) {
        // Get sum of all X
        double sum = 0;
        for(int i=0;i<allX.length;i++){
            sum += Math.exp( allX[i] );
        }
        // Get simple element
        double eX = Math.exp(x);
        // Compute result
        double result = eX/sum;
        // Normalize result from -1.0 to +1.0
        return (2*result)-1;
    }

    @Override
    public double derivative(double x, double[] allX) {
        // Get sum of all X
        double v = 0;
        for(int i=0;i<allX.length;i++){
            v += Math.exp( allX[i] );
        }
        // Get X element
        double u = Math.exp(x);
        // Compute u'
        double up = u;
        // compute v'
        double vp = u;
        // Compute (u/v)' = (u'v - uv') / v²
        double result = ((up*v)-(u*vp))/(v*v);
        // return result
        return result;
    }

}
