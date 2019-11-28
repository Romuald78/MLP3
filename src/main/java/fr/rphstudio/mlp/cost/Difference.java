package fr.rphstudio.mlp.cost;

public class Difference implements CostFunction {

    @Override
    public double function(double x, double err) {
        return (err-x);
    }

    @Override
    public double derivative(double x, double err) {
        return 1;
    }

}
