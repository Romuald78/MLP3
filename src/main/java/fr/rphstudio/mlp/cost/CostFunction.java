package fr.rphstudio.mlp.cost;

public interface CostFunction {

    public double function  (double x, double err);
    public double derivative(double x, double err);

}
