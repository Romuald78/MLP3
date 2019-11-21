/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.rphstudio.launcher;

import fr.rphstudio.mlp.MLP;
import fr.rphstudio.mlp.activation.ActivationFunction;
import fr.rphstudio.mlp.activation.Sigmoid;
import fr.rphstudio.mlp.activation.SoftMax;
import fr.rphstudio.mlp.cost.CostFunction;
import fr.rphstudio.mlp.cost.Difference;
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
    // PRIVATE STUCTURES
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



    private static final int LAYER_SPACE = 20;
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
        this.layers.add( new LayerStruct(2, null ) ); // no activation function because this is the input layer
        this.layers.add( new LayerStruct(2, new Sigmoid() ) );
        this.layers.add( new LayerStruct(2, new Sigmoid() ) );
        this.layers.add( new LayerStruct(1, new Sigmoid() ) );
        // Init cost function
        this.cf = new Difference();

        // Init the application
        this.init();

        // init timewatch
        this.timeWatch = 0;
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
        // this.mlp.scramble();


        // Set weights for debug
        this.mlp.setWeight(1, 0, 0, 0.5 );
        this.mlp.setWeight(1, 0, 1, 1.5 );
        this.mlp.setWeight(1, 1, 0, -1 );
        this.mlp.setWeight(1, 1, 1, -2 );
        this.mlp.setWeight(2, 0, 0, 1 );
        this.mlp.setWeight(2, 0, 1, 3 );
        this.mlp.setWeight(2, 1, 0, -1 );
        this.mlp.setWeight(2, 1, 1, -4 );
        this.mlp.setWeight(3, 0, 0, 1 );
        this.mlp.setWeight(3, 0, 1, -3 );



        // TRAIN
        double[] input  = {2, -1};
        double[] output = { 1 };
        double learningRate = 0.1;

        this.mlp.setInputs(input);
        this.mlp.processForward();
        this.mlp.backPropagation(output, learningRate);

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
            int v = (int)(255*this.mlp.getWeight(numLayer, numNeuron, i));
            Color clr = new Color(v,v,v);
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
        int v = (int)(255*this.mlp.getOutput(numLayer,numNeuron));
        Color clr = new Color(v,v,0);
        g.setColor(clr);
        g.fillOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
    }

    private void renderInput( Graphics g, float x, float y, int numInput ){
        // update ref position
        y += numInput*(NEURON_HEIGHT+LAYER_SPACE);
        g.setColor(Color.blue);
        g.drawRect(x,y,NEURON_WIDTH,NEURON_HEIGHT);
        // Draw OUT circle
        int v = (int)(255*this.mlp.getOutput(0,numInput));
        Color clr = new Color(0,v,v);
        g.setColor(clr);
        g.fillOval(x+DZ+DAF+DAF-(OUT_WIDTH/2),y+(NEURON_HEIGHT-OUT_HEIGHT)/2,OUT_WIDTH,OUT_HEIGHT);
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
        for(int i=0;i<this.mlp.getNbLayers();i++){
            // Get number of neurons for this layer
            int N = this.mlp.getNbNeurons(i);
            // Get y ref for this layer
            float h    = LAYER_SPACE+N*(NEURON_HEIGHT+LAYER_SPACE);
            float midY = MID_Y - (h/2);
            // convert i to outX (output position of a layer)
            double outX = REF_X+(i*(LAYER_WIDTH+LAYER_INTER))+LAYER_WIDTH-LAYER_SPACE;
            // Check each neuron in the current layer
            for(int j=0;j<N;j++){
                // convert j to outY (output position of the current neuron of the current layer)
                double outY = midY+(j*(NEURON_HEIGHT+LAYER_SPACE))+(NEURON_HEIGHT/2)+LAYER_SPACE;
                this.gameObject.getContainer().getGraphics().setColor(Color.green);
                this.gameObject.getContainer().getGraphics().drawRect((float)(outX)-OUT_BOX/2.0f,(float)(outY)-OUT_BOX/2.0f,OUT_BOX,OUT_BOX);
                if( x>= (float)(outX)-OUT_BOX/2 && x<= (float)(outX)+OUT_BOX/2 && y>= (float)(outY)-OUT_BOX/2 && y<= (float)(outY)+OUT_BOX/2 ){
                    msg = String.format("%.6f", this.mlp.getOutput(i,j));
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
            g.setColor(new Color(0,0,0,160));
            g.fillRect(x-15,y-25,80,25);
            g.setColor(Color.white);
            g.drawString(msg,x-10,y-20);
        }
        else{
            //*
            g.setColor(Color.magenta);
            g.fillRect(x-10,y-10,20,20);
            g.drawString(x+"/"+y, x-100,y-25);
            //*/
        }

        // Render version number
        g.setColor(Color.white);
        g.drawString(this.version, 1920-500, 1080-30);
    }

    
    //------------------------------------------------
    // UPDATE METHOD
    //------------------------------------------------
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
    {

        /*
        // measure time
        this.timeWatch += delta;

        // check when we have to perform operation
        if(this.timeWatch > 100){
            this.timeWatch -= 100;
            double[] input = {Math.random(), Math.random()};
            this.mlp.setInputs(input);
            this.mlp.processForward();
        }
        //*/


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
            case Input.KEY_F11:
                try {
                    this.container.setFullscreen(!this.container.isFullscreen());
                }catch(SlickException se){}
                break;
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