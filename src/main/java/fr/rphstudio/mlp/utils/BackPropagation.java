package fr.rphstudio.mlp.utils;

import fr.rphstudio.mlp.activation.ActivationFunction;
import fr.rphstudio.mlp.cost.CostFunction;

import java.util.InputMismatchException;

public class BackPropagation {

    // Compute DJA using output and expected output (cost function)
    static public double[] computeDJA(double[] A, double[] E, CostFunction cf){
        // check lengths match
        if( A.length != E.length ){
            throw new InputMismatchException("Expected output size does not match the last layer output size !");
        }
        // Store N
        int N = A.length;
        // allocate output
        double[] DJA = new double[N];
        // for each output, compute the cost function derivative
        for(int i=0;i<N;i++){
            DJA[i] = cf.derivative(A[i],E[i]) / N;
        }
        // return result
        return DJA;
    }

    // compute DJZ using previous DJA and activation function with Z
    static public double[] computeDJZ(double[] DJA, double[] Z, ActivationFunction af){
        // check lengths match
        if( DJA.length != Z.length ){
            throw new InputMismatchException("DJA size does not match Z size !");
        }
        // Store N
        int N = DJA.length;
        // allocate output
        double[] DJZ = new double[N];
        // compute each entry
        for(int i=0;i<N;i++){
            DJZ[i] = DJA[i] * af.derivative(Z[i],Z);
        }
        // return result
        return DJZ;
    }

    // Compute DJW for current layer, using the previous layer size and the current layer DJZ
    // dimensions are [layer index ][weight index] or [layer index ][previous output index]
    static public double[][] computeDJW( double[] DJZ, double[] prevA ){
        // Store N (number of neurons)
        int N = DJZ.length;
        // Store O (number of previous outputs)
        int O = prevA.length;
        // allocate output
        double[][] DJW = new double[N][O];
        // Compute each entry
        for(int n=0;n<N;n++){
            for(int o=0;o<O;o++){
                DJW[n][o] = DJZ[n]*prevA[o];
            }
        }
        // return result
        return DJW;
    }

    // Compute DJB from the whole DJZ
    static public double[] computeDJB(double[] DJZ){
        // Store N (number of neurons)
        int N = DJZ.length;
        // Loop ad compute sum of this array
        double DJB[] = new double[N];
        for(int i=0;i<N;i++){
            DJB[i] = DJZ[i];
        }
        // return result
        return DJB;
    }

    // Compute DJA for a layer, from the next layer DJZ and weights
    static public double[] computeDJA(double[] DJZ, double[][] W){

        // check the first dimension of W is the same than DJZ
        if( DJZ.length != W.length ){
            throw new InputMismatchException("DJZ size does not match the first dimension size of W !");
        }
        // Get DJZ Length (number of neurons for the current layer)
        // it is also the first W dimension
        int curSize = DJZ.length;
        // Get the W second dimension size (number of neurons for the previous layer,
        // or the number of weights for each neuron of the current layer)
        int prevSize = W[0].length;
        // Allocate output DJA
        double[] DJA = new double[prevSize];
        // Process the DJA values
        for(int p=0;p<prevSize;p++){
            DJA[p] = 0;
            for(int c=0;c<curSize;c++){
                DJA[p] += DJZ[c] * W[c][p];
            }
        }
        // Return result
        return DJA;
    }


}
