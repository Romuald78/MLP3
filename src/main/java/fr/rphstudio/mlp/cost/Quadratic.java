package fr.rphstudio.mlp.cost;

import fr.rphstudio.mlp.activation.ActivationFunction;

public class Quadratic implements CostFunction {

    @Override
    public double function(double x, double err) {
        return (x-err)*(x-err);
    }

    @Override
    public double derivative(double x, double err) {
        return 2*(x-err);
    }

}
