package fr.rphstudio.mlp.cost;

import java.io.Serializable;

public interface CostFunction extends Serializable {

    public double function  (double x, double err);
    public double derivative(double x, double err);

}
