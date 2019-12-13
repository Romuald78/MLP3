package fr.rphstudio.mlp.structure;

import fr.rphstudio.mlp.utils.Console;

import java.util.InputMismatchException;

public class InputLayer {

    // Properties
    public double[]   A;

    // constructor
    public InputLayer(int curLayerSize){
        this.A  = new double[curLayerSize];
    }

    // getters
    public double getSize(){
        return this.A.length;
    }

    // Setters
    public void setInputs(double[] X){
        // check the input size is the same than weight size
        if( X.length != this.A.length ){
            throw new InputMismatchException("Input size does not match the first layer size !");
        }
        this.A = X;
    }
    public void setInput(int y, double x){
        this.A[y] = x;
    }

    public String toString(){
        String out = "";
        out += Console.BOLD+Console.BLUE;
        out += "----------------------------\n";
        out += "----- LAYER (size="+String.format("%3d", this.A.length)+") -----\n" ;
        out += "----------------------------\n";
        // Input values
        out += Console.BOLD+Console.RED+"[>] INPUTS : \n"+Console.YELLOW;
        out += "    ";
        for(int y=0;y<this.A.length;y++){
            out += String.format("%.14f", this.A[y])+"\t";
        }
        out += "\n"+Console.RESET;
        return out;
    }
}
