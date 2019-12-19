package fr.rphstudio.mlp.utils;

import fr.rphstudio.mlp.MLP;
import fr.rphstudio.mlp.training.ITraining;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SlickDisplayMLP {

    //------------------------------------------------
    // PRIVATE CONSTANTS
    //------------------------------------------------
    private static final int CIRCLE_WIDTH  = 15;
    private static final int CIRCLE_HEIGHT = CIRCLE_WIDTH;
    private static final int DZ  = 20;
    private static final int DAF = 20;
    private static final int NEURON_WIDTH  = DZ+DAF*2;
    private static final int NEURON_HEIGHT = NEURON_WIDTH/2;

    private static final int OUT_WIDTH  = 10;
    private static final int OUT_BOX    = 30;       // out box is the hit box used when mouse is over
    private static final int OUT_HEIGHT = OUT_WIDTH;

    private static final int LAYER_SPACE = 7;
    private static final int LAYER_WIDTH = 2*LAYER_SPACE + NEURON_WIDTH;
    private static final int LAYER_INTER = 125;


    public static void renderNeuron(MLP mlp, Graphics g, float x, float y, int numLayer, int numNeuron, String label, float refX, float midY ){
        // update ref position
        y += numNeuron*(NEURON_HEIGHT+LAYER_SPACE);

        // Get number of output from previous layer
        int N = mlp.getNbNeurons(numLayer-1);
        // Draw all weights
        for(int i=0;i<N;i++){
            // Compute x and y ref from previous
            float hPrev = LAYER_SPACE+N*(NEURON_HEIGHT+LAYER_SPACE);
            float yPrev = midY - (hPrev/2) + i*(NEURON_HEIGHT+LAYER_SPACE)+(NEURON_HEIGHT/2)+LAYER_SPACE;
            float xPrev = x-LAYER_INTER-(2*LAYER_SPACE);
            int v = (int)(128*mlp.getWeight(numLayer, numNeuron, i))+127;
            Color clr = new Color(v,v,v,255);
            g.setColor(clr);
            g.drawLine(x+DZ,y+(NEURON_HEIGHT/2),xPrev,yPrev);
        }
        // Draw neuron border
        g.setColor(Color.yellow);
        g.drawRect(x,y,NEURON_WIDTH,NEURON_HEIGHT);
        // Draw Z circle
        g.fillOval(x+DZ-(CIRCLE_WIDTH/2),y+(NEURON_HEIGHT-CIRCLE_HEIGHT)/2,CIRCLE_WIDTH,CIRCLE_HEIGHT);
        // Draw AF circle
        g.fillOval(x+DZ+DAF-(CIRCLE_WIDTH/2),y+(NEURON_HEIGHT-CIRCLE_HEIGHT)/2,CIRCLE_WIDTH,CIRCLE_HEIGHT);
        // Draw OUT circle
        int v = (int)(128*mlp.getOutput(numLayer,numNeuron))+127;
        Color clr = new Color(v,v,0);
        g.setColor(clr);
        g.fillOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
        g.setColor(Color.yellow);
        g.drawOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
        // draw label
        if(label != null){
            g.drawString(label, x+DZ+DAF+DAF + 15, y+(NEURON_HEIGHT-OUT_HEIGHT)/2 - 4 );
            g.drawString(label, x+DZ+DAF+DAF + 15, y+(NEURON_HEIGHT-OUT_HEIGHT)/2 - 4 );
        }
    }

    public static void renderInput(MLP mlp,  Graphics g, float x, float y, int numInput ){
        // update ref position
        y += numInput*(NEURON_HEIGHT+LAYER_SPACE);
        g.setColor(Color.blue);
        g.drawRect(x,y,NEURON_WIDTH,NEURON_HEIGHT);
        // Draw OUT circle
        int v = (int)(128*mlp.getOutput(0,numInput))+127;
        Color clr = new Color(0,v,v);
        g.setColor(clr);
        g.fillOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
        g.setColor(Color.blue);
        g.drawOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
    }

    public static void renderLayer(MLP mlp, Graphics g, float xRef, float yMid, int numLayer, ITraining trainer){
        String[] labels = trainer.getOutputLabels();
        int N = 0;
        if(numLayer == 0){
            N = mlp.getNbInput();
        }
        else {
            N = mlp.getNbNeurons(numLayer);
        }
        // prepare position and dimensions
        float w = LAYER_WIDTH;
        float h = LAYER_SPACE+N*(NEURON_HEIGHT+LAYER_SPACE);
        float y = yMid - (h/2);
        float x = xRef + numLayer*(LAYER_WIDTH+LAYER_INTER);
        // display layer
        g.setColor(Color.cyan);
        g.drawRect(x, y, w, h);
        // Display each neuron or input
        if(numLayer==0){
            for (int row = 0; row < N; row++) {
                SlickDisplayMLP.renderInput(mlp, g, x + LAYER_SPACE + numLayer * (LAYER_WIDTH + LAYER_INTER), y + LAYER_SPACE, row);
            }
        }
        else {
            for (int row = 0; row < N; row++) {
                String label = null;
                if(labels != null){
                    if(labels.length == N){
                        label = labels[row];
                    }
                }
                SlickDisplayMLP.renderNeuron(mlp, g, x + LAYER_SPACE, y + LAYER_SPACE, numLayer, row, label, xRef, yMid);
            }
        }
    }


    // Get MLP information and display neurons according to
    public static void displayMLP(MLP mlp, Graphics g, ITraining trainer, float refX, float midY){
        int L = mlp.getNbLayers();
        for(int i=L-1;i>=0;i--){
            SlickDisplayMLP.renderLayer(mlp, g, refX,midY, i, trainer);
        }
    }


    public static String getMessage(MLP mlp, float x, float y, boolean displayHelp, float refX, float midY){
        String msg = null;
        // check each X output
        for(int layerNum=0;layerNum<mlp.getNbLayers();layerNum++){
            // Get number of neurons for this layer
            int N = mlp.getNbNeurons(layerNum);
            // Get y ref for this layer
            float h    = LAYER_SPACE+N*(NEURON_HEIGHT+LAYER_SPACE);
            midY = midY - (h/2);
            // convert i to outX (output position of a layer)
            double outX = refX +(layerNum*(LAYER_WIDTH+LAYER_INTER))+LAYER_WIDTH-LAYER_SPACE;
            // Check each neuron in the current layer
            for(int neuronNum=0;neuronNum<N;neuronNum++){
                // convert j to outY (output position of the current neuron of the current layer)
                double outY = midY+(neuronNum*(NEURON_HEIGHT+LAYER_SPACE))+(NEURON_HEIGHT/2)+LAYER_SPACE;
                //this.gameObject.getContainer().getGraphics().setColor(Color.green);
                //this.gameObject.getContainer().getGraphics().drawRect((float)(outX)-OUT_BOX/2.0f,(float)(outY)-OUT_BOX/2.0f,OUT_BOX,OUT_BOX);
                if( x>= (float)(outX)-OUT_BOX/2 && x<= (float)(outX)+OUT_BOX/2 && y>= (float)(outY)-OUT_BOX/2 && y<= (float)(outY)+OUT_BOX/2 ){
                    msg  = String.format("NEURON %d-%d\n", layerNum, neuronNum);
                    msg += String.format("A=%.6f\n", mlp.getOutput(layerNum,neuronNum));
                    if(displayHelp){
                        msg += String.format("Z=%.6f\n", mlp.getNetwork(layerNum,neuronNum));
                        if(layerNum>0){
                            for(int weightNum=0;weightNum<mlp.getNbNeurons(layerNum-1);weightNum++){
                                msg += String.format("W%d=%.6f\n",weightNum, mlp.getWeight(layerNum,neuronNum,weightNum));
                            }
                        }
                    }
                }
            }
        }
        return msg;
    }



}
