package fr.rphstudio.mlp;

import fr.rphstudio.mlp.activation.ActivationFunction;
import fr.rphstudio.mlp.utils.Console;

import java.util.InputMismatchException;

public class NeuronLayer {

    // Properties
    public double[]   A;
    public double[]   Z;
    // The W matrix has the following dimensions : [number of neurons][number of previous layer outputs]
    // or [current layer size][previous layer size]
    public double[][] W;
    public double[]   B;
    public ActivationFunction af;

    // constructor
    public NeuronLayer(int curLayerSize, int prevLayerSize, ActivationFunction activFunc){
        this.A  = new double[curLayerSize];
        this.Z  = new double[curLayerSize];
        this.W  = new double[curLayerSize][prevLayerSize];
        this.B  = new double[curLayerSize];
        this.af = activFunc;
    }

    // getters
    public double getSize(){
        return this.A.length;
    }

    // Put random values into weights and bias
    public void scramble(){
        for(int y=0;y<this.W.length;y++){
            // Randomize W
            for(int x=0;x<this.W[0].length;x++){
                this.W[y][x] = Math.random();
            }
            // Randomize B
            this.B[y] = Math.random();
        }
    }

    public void processForward(double[] inputs){
        // check the input size is the same than weight size
        if( inputs.length != this.W[0].length ){
            throw new InputMismatchException();
        }
        // For each neuron of the layer
        for(int y=0;y<this.Z.length;y++){
            // process the sum of products (weights)
            this.Z[y] = 0;
            for(int x=0;x<inputs.length;x++){
                this.Z[y] += inputs[x]*this.W[y][x];
            }
            // Add bias
            this.Z[y] += this.B[y];
        }
        // process activation function and store neuron output (after all Z are computed,
        // in order to be able to use multi class (softmax)
        for(int y=0;y<this.Z.length;y++) {
            this.A[y] = this.af.function(this.Z[y], this.Z);
        }


    }

    public void updateWeightsAndBias(double[][] weightModifiers, double[] biasModifier, double learningRate){
        // check dimensions
        if(    weightModifiers.length    != this.W.length
            || weightModifiers[0].length != this.W[0].length
            || biasModifier.length       != this.B.length){
            throw new InputMismatchException("Weight modifier size does not match current layer size!");
        }
        // Loop for all weights and update them
        for(int y=0;y<this.W.length;y++){
            for(int x=0;x<this.W[y].length;x++){
                this.W[y][x] -= weightModifiers[y][x]*learningRate;
            }
            this.B[y] -= biasModifier[y]*learningRate;
        }
    }

    public String toString(){
        String out = "";
        out += Console.BOLD+Console.BLUE;
        out += "----------------------------\n";
        out += "----- LAYER (size="+String.format("%3d", this.A.length)+") -----\n" ;
        out += "----------------------------\n";
        // Weight values
        out += Console.BOLD+Console.RED+"[>] WEIGHTS (W) : \n"+Console.YELLOW;
        for(int y=0;y<this.W.length;y++){
            String tmp = "    ";
            for(int x=0;x<this.W[y].length;x++){
                tmp += String.format("%.14f", this.W[y][x])+"\t";
            }
            out += tmp+"\n";
        }
        // Bias value
        out += Console.BOLD+Console.RED+"[>] BIAS (B) : \n"+Console.YELLOW;
        out += "    " ;
        for(int y=0;y<this.B.length;y++) {
            out += String.format("%.14f", this.B[y]) + "\t";
        }
        out += "\n";
        // Z Value (sum of all weights * inputs)
        out += Console.BOLD+Console.RED+"[>] NETWORK (Z) : \n"+Console.YELLOW;
        String tmp = "    ";
        for(int y=0;y<this.Z.length;y++){
            tmp += String.format("%.14f", this.Z[y])+"\t";
        }
        out += tmp+"\n"+Console.RESET;
        // output values
        out += Console.BOLD+Console.RED+"[>] OUTPUT (A) : \n"+Console.YELLOW;
        tmp = "    ";
        for(int y=0;y<this.A.length;y++){
            tmp += String.format("%.14f", this.A[y])+"\t";
        }
        out += tmp+"\n"+Console.RESET;
        // return result
        return out;
    }

}
