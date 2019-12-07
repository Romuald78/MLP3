/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.rphstudio.launcher;

import fr.rphstudio.misc.LCD7;
import fr.rphstudio.mlp.MLP;
import fr.rphstudio.mlp.activation.ActivationFunction;
import fr.rphstudio.mlp.activation.TanH;
import fr.rphstudio.mlp.cost.CostFunction;
import fr.rphstudio.mlp.cost.Quadratic;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class State01Start extends BasicGameState
{
    //------------------------------------------------
    // PRIVATE STRUCTURES
    //------------------------------------------------
    private class LayerStruct{
        private LayerStruct(int sz, ActivationFunction a){
            this.layerSize = sz;
            this.af = a;
        }
        private int                layerSize;
        private ActivationFunction af;
    }


    //------------------------------------------------
    // PUBLIC CONSTANTS
    //------------------------------------------------
    public static final int ID = 100;



    //------------------------------------------------
    // PRIVATE CONSTANTS
    //------------------------------------------------
    private static final int REF_X = 150;
    private static final int MID_Y = 1080/2;


    private static final int CIRCLE_WIDTH  = 18;
    private static final int CIRCLE_HEIGHT = CIRCLE_WIDTH;
    private static final int DZ  = 25;
    private static final int DAF = 25;
    private static final int NEURON_WIDTH  = DZ+DAF*2;
    private static final int NEURON_HEIGHT = NEURON_WIDTH/2;

    private static final int OUT_WIDTH  = 13;
    private static final int OUT_BOX    = 30;       // out box is the hit box used when mouse is over
    private static final int OUT_HEIGHT = OUT_WIDTH;



    private static final int LAYER_SPACE = 10;
    private static final int LAYER_WIDTH = 2*LAYER_SPACE + NEURON_WIDTH;
    private static final int LAYER_INTER = 150;


    //------------------------------------------------
    // PRIVATE PROPERTIES
    //------------------------------------------------
    private StateBasedGame gameObject;
    private GameContainer  container;
    private String         version;

    private List<LayerStruct> layers;
    private CostFunction      cf;
    private MLP               mlp;

    private long              timeWatch;
    private boolean           displayHelp;




    //------------------------------------------------
    // PRIVATE METHODS
    //------------------------------------------------
    // Get current program version string from file
    private void getVersion()
    {
        // Get display version
        BufferedReader br = null;
        try
        {
            this.version = "";
            br = new BufferedReader(new FileReader("info/version.txt"));
            String line;
            line = br.readLine();
            while(line != null)
            {
                this.version = this.version + line + "\n";
                line = br.readLine();
            }
            if (br != null)
            {
                br.close();
            }
        }
        catch (IOException e)
        {
            throw new Error(e);
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException ex)
            {
                throw new Error(ex);
            }
        }
    }

    // Quit game
    private void quitGame()
    {
        this.container.exit();
    }




    
    //------------------------------------------------
    // CONSTRUCTOR
    //------------------------------------------------
    public State01Start()
    {
    }
    
    
    //------------------------------------------------
    // INIT METHOD
    //------------------------------------------------
    public void init(GameContainer container, StateBasedGame sbGame) throws SlickException
    {
        // Init fields
        this.container  = container;
        this.gameObject = sbGame;
        
        // Get version string
        this.getVersion();

        // initializes the layer sizes to 1 input, and 1 output layer
        this.layers = new ArrayList<>();
        this.layers.add( new LayerStruct(2 , null ) ); // no activation function because this is the input layer
        this.layers.add( new LayerStruct(3, new TanH() ) );
        this.layers.add( new LayerStruct(3, new TanH() ) );
        this.layers.add( new LayerStruct(1, new TanH() ) );
        // Init cost function
        this.cf = new Quadratic();

        // Init the application
        this.init();

        // init timewatch
        this.timeWatch = 0;
        // init help display
        this.displayHelp = false;
    }

    public void init()
    {
        // Create array of sizes and activation functions
        int[] sizes = new int[this.layers.size()];
        ActivationFunction[] afs = new ActivationFunction[this.layers.size()-1];
        // fill arrays
        for(int i=0;i<this.layers.size();i++){
            sizes[i] = this.layers.get(i).layerSize;
            if(i>0){
                afs[i-1] = this.layers.get(i).af;
            }
        }
        // instanciate MLP
        this.mlp = new MLP( sizes, afs, this.cf );
        this.mlp.scramble();




        /*
        // TRAIN 3 SIMPLE VALUES
        double learningRate = 0.1;
        double[] input1 = {-1.00, -0.50};
        double[] input2 = {-0.25,  0.25};
        double[] input3 = { 0.75,  0.66};
        double[] out1   = {-0.33, -0.66};
        double[] out2   = {-0.10,  0.10};
        double[] out3   = { 0.90,  0.80};

        double err = 10000;
        long   nbTrainings = 0;
        while(err >= 0.0000001){
            err = 0;
            this.mlp.setInputs(input1);
            this.mlp.processForward();
            err += this.mlp.backPropagation(out1, learningRate);
            this.mlp.setInputs(input2);
            this.mlp.processForward();
            err += this.mlp.backPropagation(out2, learningRate);
            this.mlp.setInputs(input3);
            this.mlp.processForward();
            err += this.mlp.backPropagation(out3, learningRate);
            nbTrainings += 3;
        }
        System.out.println("Number of trainings = "+nbTrainings);
        //*/


        /*
        // TRAIN LCD 7 Segment
        double learningRate = 0.2;
        double err    = 10000;
        double errMin = 10000;
        while(err >= 180){
            err = 0;
            double[] input = null;
            double[] output = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
            for(int x=0; x<10; x++){
                input = LCD7.getDigitInput(x);
                output[x] = 1;
                this.mlp.setInputs(input);
                this.mlp.processForward();
                err += this.mlp.backPropagation(output, learningRate);
            }
            learningRate = err/1000;
            if(errMin > err){
                errMin = err;
                System.out.println(errMin);
            }
            else{
                System.out.println(errMin);
            }
        }
        //*/


        //* TRAIN XOR
        double learningRate = 0.1;
        double[] input00 = {-1, -1};
        double[] input10 = { 1, -1};
        double[] input01 = {-1,  1};
        double[] input11 = { 1,  1};
        double[] out0   = {-1};
        double[] out1   = { 1};

        double err = 1;
        long   nbTrainings = 0;
        while(err >= 0.001) {
            err = 0;
            this.mlp.setInputs(input00);
            this.mlp.processForward();
            err += this.mlp.backPropagation(out0, learningRate);
            this.mlp.setInputs(input10);
            this.mlp.processForward();
            err += this.mlp.backPropagation(out1, learningRate);
            this.mlp.setInputs(input01);
            this.mlp.processForward();
            err += this.mlp.backPropagation(out1, learningRate);
            this.mlp.setInputs(input11);
            this.mlp.processForward();
            err += this.mlp.backPropagation(out0, learningRate);
            System.out.println(err);
        }
        //*/

        // display final MLP configuration
        System.out.println(this.mlp);
    }

    private void renderNeuron( Graphics g, float x, float y, int numLayer, int numNeuron ){
        // update ref position
        y += numNeuron*(NEURON_HEIGHT+LAYER_SPACE);

        // Get number of output from previous layer
        int N = this.mlp.getNbNeurons(numLayer-1);
        // Draw all weights
        for(int i=0;i<N;i++){
            // Compute x and y ref from previous
            float hPrev = LAYER_SPACE+N*(NEURON_HEIGHT+LAYER_SPACE);
            float yPrev = MID_Y - (hPrev/2) + i*(NEURON_HEIGHT+LAYER_SPACE)+(NEURON_HEIGHT/2)+LAYER_SPACE;
            float xPrev = x-LAYER_INTER-(2*LAYER_SPACE);
            int v = (int)(128*this.mlp.getWeight(numLayer, numNeuron, i))+127;
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
        int v = (int)(128*this.mlp.getOutput(numLayer,numNeuron))+127;
        Color clr = new Color(v,v,0);
        g.setColor(clr);
        g.fillOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
        g.setColor(Color.yellow);
        g.drawOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
    }

    private void renderInput( Graphics g, float x, float y, int numInput ){
        // update ref position
        y += numInput*(NEURON_HEIGHT+LAYER_SPACE);
        g.setColor(Color.blue);
        g.drawRect(x,y,NEURON_WIDTH,NEURON_HEIGHT);
        // Draw OUT circle
        int v = (int)(128*this.mlp.getOutput(0,numInput))+127;
        Color clr = new Color(0,v,v);
        g.setColor(clr);
        g.fillOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
        g.setColor(Color.blue);
        g.drawOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
    }

    private void renderLayer(Graphics g, float xRef, float yMid, int numLayer){
        int N = 0;
        if(numLayer == 0){
            N = this.mlp.getNbInput();
        }
        else {
            N = this.mlp.getNbNeurons(numLayer);
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
                this.renderInput(g, x + LAYER_SPACE + numLayer * (LAYER_WIDTH + LAYER_INTER), y + LAYER_SPACE, row);
            }
        }
        else {
            for (int row = 0; row < N; row++) {
                this.renderNeuron(g, x + LAYER_SPACE, y + LAYER_SPACE, numLayer, row);
            }
        }
    }

    private String getMessage(float x, float y){
        String msg = null;
        // check each X output
        for(int layerNum=0;layerNum<this.mlp.getNbLayers();layerNum++){
            // Get number of neurons for this layer
            int N = this.mlp.getNbNeurons(layerNum);
            // Get y ref for this layer
            float h    = LAYER_SPACE+N*(NEURON_HEIGHT+LAYER_SPACE);
            float midY = MID_Y - (h/2);
            // convert i to outX (output position of a layer)
            double outX = REF_X+(layerNum*(LAYER_WIDTH+LAYER_INTER))+LAYER_WIDTH-LAYER_SPACE;
            // Check each neuron in the current layer
            for(int neuronNum=0;neuronNum<N;neuronNum++){
                // convert j to outY (output position of the current neuron of the current layer)
                double outY = midY+(neuronNum*(NEURON_HEIGHT+LAYER_SPACE))+(NEURON_HEIGHT/2)+LAYER_SPACE;
                //this.gameObject.getContainer().getGraphics().setColor(Color.green);
                //this.gameObject.getContainer().getGraphics().drawRect((float)(outX)-OUT_BOX/2.0f,(float)(outY)-OUT_BOX/2.0f,OUT_BOX,OUT_BOX);
                if( x>= (float)(outX)-OUT_BOX/2 && x<= (float)(outX)+OUT_BOX/2 && y>= (float)(outY)-OUT_BOX/2 && y<= (float)(outY)+OUT_BOX/2 ){
                    msg  = String.format("NEURON %d-%d\n", layerNum, neuronNum);
                    msg += String.format("A=%.6f\n", this.mlp.getOutput(layerNum,neuronNum));
                    if(this.displayHelp){
                        msg += String.format("Z=%.6f\n", this.mlp.getNetwork(layerNum,neuronNum));
                        if(layerNum>0){
                            for(int weightNum=0;weightNum<this.mlp.getNbNeurons(layerNum-1);weightNum++){
                                msg += String.format("W%d=%.6f\n",weightNum, this.mlp.getWeight(layerNum,neuronNum,weightNum));
                            }
                        }
                    }
                }
            }
        }


        return msg;
    }



    //------------------------------------------------
    // RENDER METHOD
    //------------------------------------------------
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException
    {
        // Fit Screen
        MainLauncher.fitScreen(container, g);

        // Get MLP information and display neurons according to
        int L = this.mlp.getNbLayers();
        for(int i=L-1;i>=0;i--){
            this.renderLayer(g, REF_X,MID_Y, i);
        }

        // Get mouse position
        float x = container.getInput().getMouseX();
        float y = container.getInput().getMouseY();
        // Display message according to position
        String msg = this.getMessage(x,y);
        if(msg != null){
            int h  = 50;
            int dy = -55;
            if(this.displayHelp){
                h = 125;
            }
            g.setColor(new Color(0,128,0,192));
            g.fillRect(x-55,y+dy,120,h);
            g.setColor(Color.white);
            g.drawString(msg,x-50,y+dy+5);
        }

        // Render version number
        g.setColor(Color.white);
        g.drawString(this.version, 1920-500, 1080-30);
    }

    
    //------------------------------------------------
    // UPDATE METHOD
    //------------------------------------------------
    private static final int TIME_STEP = 1000;
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
    {
        // measure time
        this.timeWatch += delta;

        // check when we have to perform operation

        if(this.timeWatch > TIME_STEP){
            this.timeWatch -= TIME_STEP;



            /*
            // PROCESS SIMPLE 3 INPUTS
            double[] input1 = {-1.00, -0.50};
            double[] input2 = {-0.25,  0.25};
            double[] input3 = { 0.75,  0.66};
            double[] input = null;
            int x = Math.abs((int)(System.currentTimeMillis()/TIME_STEP))%3;
            if(x == 0){
                input = input1;
            }
            else if(x == 1){
                input = input2;
            }
            else if(x == 2){
                input = input3;
            }
            else{
                throw new Error("IMPOSSIBLE VALUE "+x);
            }
            //*/


            /*
            // Process LCD 7
            int x = (int)(System.currentTimeMillis()/TIME_STEP)%10;
            double[] input = LCD7.getDigitInput(x);
            //*/


            //*
            // PROCESS XOR
            double[] input00 = {-1, -1};
            double[] input10 = { 1, -1};
            double[] input01 = {-1,  1};
            double[] input11 = { 1,  1};

            double[] input =null;
            int x = Math.abs((int)(System.currentTimeMillis()/TIME_STEP))%4;
            if(x == 0){
                input = input00;
            }
            else if(x == 1){
                input = input10;
            }
            else if(x == 2){
                input = input01;
            }
            else if(x == 3){
                input = input11;
            }
            else{
                throw new Error("IMPOSSIBLE VALUE "+x);
            }
            //*/



            // process forward
            this.mlp.setInputs(input);
            this.mlp.processForward();

//            double out = this.mlp.getOutput( this.mlp.getNbLayers()-1, 0 );
//            System.out.println("x="+x+"/y="+y+"/o="+out);
        }
    }
    
    
    //------------------------------------------------
    // KEYBOARD METHODS
    //------------------------------------------------
    @Override
    public void keyPressed(int key, char c)
    {
        switch(key)
        {
            // Quit game by pressing escape
            case Input.KEY_ESCAPE:
                this.quitGame();
                break;
            // Toggle full screen mode ON/OFF
            case Input.KEY_F11:
                try {
                    this.container.setFullscreen(!this.container.isFullscreen());
                }catch(SlickException se){}
                break;
            // Display details (DEBUG)
            case Input.KEY_H:
                this.displayHelp = !this.displayHelp;
            // go to game
            // all other keys have no effect
            default :     
                break;        
        }
    }
    
    
    //------------------------------------------------
    // STATE ID METHOD
    //------------------------------------------------
    @Override
    public int getID()
    {
          return this.ID;
    }



    //------------------------------------------------
    // END OF STATE
    //------------------------------------------------
}