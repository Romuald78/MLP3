package fr.rphstudio.mlp;

import fr.rphstudio.mlp.activation.ActivationFunction;
import fr.rphstudio.mlp.cost.CostFunction;
import fr.rphstudio.mlp.utils.BackPropagation;
import fr.rphstudio.mlp.utils.Console;
import fr.rphstudio.mlp.structure.InputLayer;
import fr.rphstudio.mlp.structure.NeuronLayer;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class MLP {

    private InputLayer inputLayer;
    private List<NeuronLayer> neuronLayers;
    private CostFunction      costFunction;

    /**
     * length of AFS is -1 compared to length of layerSizes, because the first layer
     * is an input layer with no AF
     * @param layerSizes
     * @param afs
     * @throws InputMismatchException
     */
    public MLP(int[] layerSizes, ActivationFunction[] afs, CostFunction cf) throws InputMismatchException {
        // Check the sizes are correct
        if(layerSizes.length <= 1 ){
            throw new InputMismatchException();
        }
        // check size of AF is the same than layersizes
        if(layerSizes.length-1 != afs.length){
            throw new InputMismatchException( (layerSizes.length-1) + " / " + (afs.length) );
        }
        // check sizes for each layer, and afs are not null
        for(int i=0;i<layerSizes.length;i++){
            if(layerSizes[i] <= 0){
                throw new InputMismatchException("Layer size (nb neurons) is zero or less!");
            }
        }
        for(int i=0;i<afs.length;i++){
            if(afs[i] == null){
                throw new InputMismatchException("Activation function is null!");
            }
        }

        // Ok create first layer
        this.inputLayer = new InputLayer(layerSizes[0]);
        // Create all following
        this.neuronLayers = new ArrayList<>();
        for(int i=1;i<layerSizes.length;i++){
            this.neuronLayers.add( new NeuronLayer(layerSizes[i],layerSizes[i-1],afs[i-1]) );
        }
        // Store cost function
        this.costFunction = cf;
    }

    // Scramble weights and bias for all the layers (hidden + output)
    public void scramble(){
        for(int y=0;y<this.neuronLayers.size();y++){
            this.neuronLayers.get(y).scramble();
        }
    }

    // Setters for INPUT
    public void setInputs(double[] X){
        this.inputLayer.setInputs(X);
    }
    public void setInput(int y, double x){
        this.inputLayer.setInput(y,x);
    }

    // process forward
    public void processForward(){
        this.neuronLayers.get(0).processForward(this.inputLayer.A);
        for(int i=1;i<this.neuronLayers.size();i++){
            this.neuronLayers.get(i).processForward(this.neuronLayers.get(i-1).A);
        }
    }

    // Back propagation process
    public double backPropagation(double[] expectedOut, double learningRate){

        // Check if we have only one NEURON LAYER (+INPUT)
        // Get layer references (last and previous ones)
        int layerIndex = this.neuronLayers.size()-1;
        NeuronLayer curLayer  = this.neuronLayers.get(layerIndex);
        NeuronLayer prevLayer = null;

        //--------------------------------------------
        //--------------- Prepare DJA ----------------
        //--------------------------------------------
        // Compute DJA
        double[]   DJA = BackPropagation.computeDJA( curLayer.A, expectedOut, this.costFunction );
        double[]   DJZ = null;
        double[][] DJW = null;
        double[]   DJB = null;


        //--------------------------------------------
        //---------- Compute the last layer ----------
        //--------------------------------------------
        if(this.neuronLayers.size() > 1) {
            // Get previous layer value
            prevLayer = this.neuronLayers.get(layerIndex - 1);
            // Compute DJZ
            DJZ = BackPropagation.computeDJZ(DJA, curLayer.Z, curLayer.af);
            // Compute DJW
            DJW = BackPropagation.computeDJW(DJZ, prevLayer.A);
            // Compute DJB
            DJB = BackPropagation.computeDJB(DJZ);
            // Compute DJA for the previous layer (for the next round of backprop)
            DJA = BackPropagation.computeDJA(DJZ, curLayer.W);
            // Update Weights and BIAS of the last layer, according to DJW and DJb
            curLayer.updateWeightsAndBias(DJW, DJB, learningRate);
        }


        //-------------------------------------------------------------------------
        //---------- Compute each hidden layer (except the first hidden) ----------
        //-------------------------------------------------------------------------
        for(layerIndex=this.neuronLayers.size()-2; layerIndex>=1; layerIndex--){
            // Update current layer and previous layer
            curLayer  = this.neuronLayers.get(layerIndex);
            prevLayer = this.neuronLayers.get(layerIndex-1);
            // Compute DJZ
            DJZ = BackPropagation.computeDJZ( DJA, curLayer.Z, curLayer.af );
            // Compute DJW
            DJW = BackPropagation.computeDJW( DJZ, prevLayer.A );
            // Compute DJB
            DJB = BackPropagation.computeDJB( DJZ );
            // Compute DJA for the previous layer (for the next round of backprop)
            DJA = BackPropagation.computeDJA( DJZ, curLayer.W);
            // Update Weights and BIAS of the current layer, according to DJW and DJB
            curLayer.updateWeightsAndBias(DJW, DJB, learningRate);
        }

        //-----------------------------------------------------------------------------
        //---------- Compute first hidden layer (plugged to the input layer) ----------
        //-----------------------------------------------------------------------------
        // Update current layer and previous layer (input)
        curLayer  = this.neuronLayers.get(0);
        // Compute DJZ
        DJZ = BackPropagation.computeDJZ( DJA, curLayer.Z, curLayer.af );
        // Compute DJW
        DJW = BackPropagation.computeDJW( DJZ, this.inputLayer.A );
        // Compute DJb
        DJB = BackPropagation.computeDJB( DJZ );
        // Update Weights and BIAS of the current layer, according to DJW and DJb
        curLayer.updateWeightsAndBias(DJW, DJB, learningRate);

        // Return the Cost function return value
        layerIndex = this.neuronLayers.size()-1;
        curLayer  = this.neuronLayers.get(layerIndex);
        double result = 0;
        for(int i=0;i<curLayer.getSize();i++){
            result += this.costFunction.function(curLayer.A[i], expectedOut[i] );
        }
        return result;
    }

    public int getNbLayers(){
        return this.neuronLayers.size()+1;
    }

    public int getNbNeurons(int numLayer){
        if(numLayer==0){
            return this.getNbInput();
        }
        else{
            return (int)(this.neuronLayers.get(numLayer-1).getSize());
        }
    }

    public int getNbInput(){
        return (int)(this.inputLayer.getSize());
    }

    public double getWeight(int numLayer, int numNeuron, int numWeight){
        return this.neuronLayers.get(numLayer-1).W[numNeuron][numWeight];
    }

    public double getOutput(int numLayer, int numNeuron){
        if(numLayer == 0){
            return this.inputLayer.A[numNeuron];
        }
        else{
            return this.neuronLayers.get(numLayer-1).A[numNeuron];
        }
    }

    public double getNetwork(int numLayer, int numNeuron){
        if(numLayer == 0){
            return -1;
        }
        else{
            return this.neuronLayers.get(numLayer-1).Z[numNeuron];
        }
    }

    public double[] getWeightExtremum(){
        double mini = 1000000000;
        double maxi = -1000000000;
        for(int lay=1;lay<this.getNbLayers();lay++){
            for(int i=0;i<this.neuronLayers.get(lay-1).W.length;i++) {
                for (int j = 0; j < this.neuronLayers.get(lay - 1).W[0].length; j++) {
                    double w = this.neuronLayers.get(lay-1).W[i][j];
                    if(w<mini){
                        mini = w;
                    }
                    if(w>maxi){
                        maxi = w;
                    }
                }
            }
        }
        double[] result = {mini,maxi};
        return result;
    }

    public void setWeight(int numLayer, int numNeuron, int prevNumNeuron, double value){
        this.neuronLayers.get(numLayer-1).W[numNeuron][prevNumNeuron] = value;
    }




    public String toString(){
        String out = "";
        out += Console.YELLOW;
        out += "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n";
        out += "@@@@@@@@@@                      DISPLAY NEURAL NETWORK INFORMATION                 @@@@@@@@@@\n";
        out += "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n";
        out += Console.RESET;
        // display input
        out += this.inputLayer.toString();
        // display each layer weights and bias
        for(int y=0;y<this.neuronLayers.size();y++){
            out += this.neuronLayers.get(y).toString();
        }
        return out;
    }

}
