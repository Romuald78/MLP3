package fr.rphstudio.mlp.cost;

public class Difference implements CostFunction {

    @Override
    public double function(double x, double err) {
        return (x-err);
    }

    @Override
    public double derivative(double x, double err) {
        return 1;
    }

}
